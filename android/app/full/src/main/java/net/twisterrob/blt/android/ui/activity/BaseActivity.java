package net.twisterrob.blt.android.ui.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import net.twisterrob.blt.android.app.full.R;

public abstract class BaseActivity extends AppCompatActivity {
	@Override public void setContentView(@LayoutRes int layoutResID) {
		super.setContentView(R.layout.activity_common);
		ViewGroup content = (ViewGroup)findViewById(R.id.layout__common_content);
		getLayoutInflater().inflate(layoutResID, content, true);
	}

	protected void resetToList() {
		View original = findViewById(android.R.id.list);
		ViewGroup parent = (ViewGroup)original.getParent();
		int index = parent.indexOfChild(original);
		parent.removeViewAt(index);
		ListView list = new ListView(this);
		list.setId(original.getId());
		parent.addView(list, index, original.getLayoutParams());
	}
}
