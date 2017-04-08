package cn.labelnet.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import cn.labelnet.event.LrcPlayToEnd;
import cn.labelnet.event.LrcViewToMusicActivity;
import cn.labelnet.maskmusic.R;
import cn.labelnet.net.MusicAsyncGetUrl;
import cn.labelnet.net.MusicAsyncHandlerGetLrc;
import cn.labelnet.net.MusicRequest;

/**
 * LrcView
 */
public class LrcView extends View implements MusicAsyncGetUrl, LrcPlayToEnd {
	private static final String TAG = LrcView.class.getSimpleName();
	private static final int MSG_NEW_LINE = 0;
	private List<Long> mLrcTimes;
	private List<String> mLrcTexts;
	private LrcHandler mHandler;
	private Paint mNormalPaint;
	private Paint mCurrentPaint;
	private float mTextSize;
	private float mDividerHeight;
	private long mAnimationDuration;
	private long mNextTime = 0l;
	private int mCurrentLine = 0;
	private float mAnimOffset;
	private boolean mIsEnd = false;
	// 网络
	private MusicAsyncHandlerGetLrc musicAsyncHandlerGetLrc;
	private MusicRequest musicRequest;
	// 回调事件
	private LrcViewToMusicActivity lrcViewToMusicActivity;
	private boolean isLrc = false;

	public void setLrcViewToMusicActivity(
			LrcViewToMusicActivity lrcViewToMusicActivity) {
		this.lrcViewToMusicActivity = lrcViewToMusicActivity;
	}

	private String songId = 001 + "";

	public LrcView(Context context) {
		this(context, null);
	}

	public LrcView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(attrs);
	}

	/**
	 * 初始化
	 *
	 * @param attrs
	 *            attrs
	 */
	private void init(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs,
				R.styleable.LrcView);
		mTextSize = ta.getDimension(R.styleable.LrcView_textSize, 48.0f);
		mDividerHeight = ta.getDimension(R.styleable.LrcView_dividerHeight,
				72.0f);
		mAnimationDuration = ta.getInt(R.styleable.LrcView_animationDuration,
				1000);
		mAnimationDuration = mAnimationDuration < 0 ? 1000 : mAnimationDuration;
		// int normalColor = ta.getColor(R.color.app_color_whrit,
		// 0xffffffff);
		// int currentColor = ta.getColor(R.color.app_color,
		// 0xffff4081);
		ta.recycle();

		mLrcTimes = new ArrayList<Long>();
		mLrcTexts = new ArrayList<String>();
		WeakReference<LrcView> lrcViewRef = new WeakReference<LrcView>(this);
		mHandler = new LrcHandler(lrcViewRef);
		mNormalPaint = new Paint();
		mCurrentPaint = new Paint();
		mNormalPaint.setColor(Color.WHITE);
		mNormalPaint.setTextSize(mTextSize);
		mCurrentPaint.setColor(Color.RED);
		mCurrentPaint.setTextSize(mTextSize);
		// 设置网络监听
		musicAsyncHandlerGetLrc = new MusicAsyncHandlerGetLrc();
		musicAsyncHandlerGetLrc.setMusicasyncGetUrl(this);
		musicRequest = new MusicRequest();
		musicRequest.setMusicAsyncHandler(musicAsyncHandlerGetLrc);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (mLrcTimes.isEmpty() || mLrcTexts.isEmpty()) {
			return;
		}

		// 中心Y坐标
		float centerY = getHeight() / 2 + mTextSize / 2 + mAnimOffset;

		// 画当前行
		String currStr = mLrcTexts.get(mCurrentLine);
		float currX = (getWidth() - mCurrentPaint.measureText(currStr)) / 2;
		canvas.drawText(currStr, currX, centerY, mCurrentPaint);

		// 画当前行上面的
		for (int i = mCurrentLine - 1; i >= 0; i--) {
			String upStr = mLrcTexts.get(i);
			float upX = (getWidth() - mNormalPaint.measureText(upStr)) / 2;
			float upY = centerY - (mTextSize + mDividerHeight)
					* (mCurrentLine - i);
			canvas.drawText(upStr, upX, upY, mNormalPaint);
		}

		// 画当前行下面的
		for (int i = mCurrentLine + 1; i < mLrcTimes.size(); i++) {
			String downStr = mLrcTexts.get(i);
			float downX = (getWidth() - mNormalPaint.measureText(downStr)) / 2;
			float downY = centerY + (mTextSize + mDividerHeight)
					* (i - mCurrentLine);
			canvas.drawText(downStr, downX, downY, mNormalPaint);
		}
	}

	/**
	 * 加载歌词文件
	 *
	 * @param lrcName
	 *            assets下的歌词文件名
	 * @throws Exception
	 */
	public void loadLrc(String lrcName) throws Exception {
		mLrcTexts.clear();
		mLrcTimes.clear();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				getResources().getAssets().open(lrcName)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] arr = parseLine(line);
			if (arr != null) {
				mLrcTimes.add(Long.parseLong(arr[0]));
				mLrcTexts.add(arr[1]);
			}
		}
		br.close();
	}

	/**
	 * 加载歌词文件
	 * 
	 * @param isr
	 * @throws Exception
	 */
	public void loadLrcByUrl(String songid) throws Exception {
		if (!songId.equals(songid)) {
			mLrcTexts.clear();
			mLrcTimes.clear();
			mNextTime = 0;
			mCurrentLine = 0;
			mIsEnd = false;
			musicRequest.requestStringLrcData(songid);
			this.songId = songid;
		}
	}

	/**
	 * 更新进度
	 *
	 * @param time
	 *            当前时间
	 */
	public synchronized void updateTime(long time) {
		// 避免重复绘制
		if (time < mNextTime || mIsEnd) {
			return;
		}
		for (int i = 0; i < mLrcTimes.size(); i++) {
			if (mLrcTimes.get(i) > time) {
				Log.i(TAG, "newline ...");
				mNextTime = mLrcTimes.get(i);
				mCurrentLine = i < 1 ? 0 : i - 1;
				// 属性动画只能在主线程使用，因此用Handler转发操作
				mHandler.sendEmptyMessage(MSG_NEW_LINE);
				break;
			} else if (i == mLrcTimes.size() - 1) {
				// 最后一行
				Log.i(TAG, "end ...");
				mCurrentLine = mLrcTimes.size() - 1;
				mIsEnd = true;
				// 属性动画只能在主线程使用，因此用Handler转发操作
				mHandler.sendEmptyMessage(MSG_NEW_LINE);
				break;
			}
		}
	}

	/**
	 * 解析一行
	 *
	 * @param line
	 *            [00:10.61]走过了人来人往
	 * @return {10610, 走过了人来人往}
	 */
	private String[] parseLine(String line) {
		Matcher matcher = Pattern.compile("\\[(\\d)+:(\\d)+(\\.)(\\d+)\\].+")
				.matcher(line);
		if (!matcher.matches()) {
			Log.e(TAG, line);
			return null;
		}
		line = line.replaceAll("\\[", "");
		String[] result = line.split("\\]");
		result[0] = parseTime(result[0]);
		return result;
	}

	/**
	 * 解析时间
	 *
	 * @param time
	 *            00:10.61
	 * @return long
	 */
	private String parseTime(String time) {
		time = time.replaceAll(":", "\\.");
		String[] times = time.split("\\.");
		long l = 0l;
		try {
			long min = Long.parseLong(times[0]);
			long sec = Long.parseLong(times[1]);
			long mil = Long.parseLong(times[2]);
			l = min * 60 * 1000 + sec * 1000 + mil * 10;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return String.valueOf(l);
	}

	/**
	 * 换行动画 Note:属性动画只能在主线程使用
	 */
	private void newLineAnim() {
		ValueAnimator animator = ValueAnimator.ofFloat(mTextSize
				+ mDividerHeight, 0.0f);
		animator.setDuration(mAnimationDuration);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				mAnimOffset = (Float) animation.getAnimatedValue();
				invalidate();
			}
		});
		animator.start();
	}

	private static class LrcHandler extends Handler {
		private WeakReference<LrcView> mLrcViewRef;

		public LrcHandler(WeakReference<LrcView> lrcViewRef) {
			mLrcViewRef = lrcViewRef;
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_NEW_LINE:
				LrcView lrcView = mLrcViewRef.get();
				if (lrcView != null) {
					lrcView.newLineAnim();
				}
				break;
			}
			super.handleMessage(msg);
		}
	}

	@Override
	public void getSongImageURL(String songLrc) {
		// 网络请求成功歌词
		// Log.d("MaskMusic", songLrc);
		parseSongLrc(songLrc);

	}

	private void parseSongLrc(String songLrc) {
		Log.d("MaskMusic", songLrc);
		String[] strs = songLrc.split("\\[");
		for (String line : strs) {
			line = ("[" + line).replace("&#58;", ":").replace("&#46;", ".")
					.replace("&#10;", "").replace("&#32;", "　")
					.replace("&#45;", "-").replace("&#40;", "")
					.replace("&#41;", "").replace("&#38;", "")
					.replace("&#59;", "").replace("&#39;", "").replace("&#13;","");
			String[] arr = parseLine(line);
			if (arr != null) {
				mLrcTimes.add(Long.parseLong(arr[0]));
				mLrcTexts.add(arr[1]);
			}
			// Log.d("MaskMusic", line);
		}
		// 回调判断有没有歌词
		if (mLrcTexts.size() > 0 && mLrcTimes.size() > 0) {
			isLrc = true;
		}
		lrcViewToMusicActivity.LrcViewIsLrc(isLrc);
	}

	@Override
	public void playToEnd() {
		// 播放完毕，进行初始化
		// Log.d("MaskMusic", "playToEnd : 播放完毕");
		mNextTime = 0;
		mCurrentLine = 0;
		mIsEnd = false;
		updateTime(mNextTime);

	}

	@Override
	public void playToPause(final long mt) {
		Log.d("MaskMusic", "mNextTime CurrentTime : " + mt);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				
				System.out.println("执行了");
				if (mLrcTexts.size() > 0 && mLrcTimes.size() > 0) {
					for (int i = 0; i < mLrcTimes.size() - 1; i++) {
						if (mt >= mLrcTimes.get(i)
								&& mt <= mLrcTimes.get(i + 1)) {
							Log.d("MaskMusic", mt + " 毫秒的歌词为 "
									+ mLrcTexts.get(i));
							mNextTime = mLrcTimes.get(i);
							mCurrentLine = i;
							updateTime(mNextTime);
						}
					}
				}else{
					lrcViewToMusicActivity.LrcViewIsLrc(false);
				}
				Log.d("MaskMusic", "playToPause over");
				
			}
		}, 2000);

		
		// 暂时没有使用
		// 遇到问题 ，从MusicService 的 时间，很难与 集合中的时间匹配成功！

	}
}