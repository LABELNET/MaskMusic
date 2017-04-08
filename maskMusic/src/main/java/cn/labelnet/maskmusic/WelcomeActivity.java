package cn.labelnet.maskmusic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class WelcomeActivity extends Activity {

	private Handler handler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				startActivity(new Intent(WelcomeActivity.this,
						MainActivity.class));
				finish();
			}
		}, 3000);

	}
}
