package cn.kenneth.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Activity implements TextWatcher, View.OnClickListener, View.OnKeyListener {

    /**
     * 话题的正则表达式
     */
    private static final String topicRegex = "#([^#]+?)#";

    private EditText mEditText;

    private Button mButton;

    /**
     * 维护文本每个话题变色span
     */
    private ArrayList<ForegroundColorSpan> mColorSpans = new ArrayList<>();
    private CheckBox mCheckBox1;
    private CheckBox mCheckBox2;
    private ArrayList<String> mTopicList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditText = (EditText) findViewById(R.id.editText);
        mEditText.addTextChangedListener(this);
        mEditText.setOnClickListener(this);
        mEditText.setOnKeyListener(this);

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(this);

        mCheckBox1 = (CheckBox) findViewById(R.id.checkBox1);
        mCheckBox2 = (CheckBox) findViewById(R.id.checkBox2);
    }


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.i("MainActivity", "onTextChanged");
        if (TextUtils.isEmpty(s)) return;
        //1,查找话题
        String content = s.toString();
        mTopicList.clear();
        mTopicList.addAll(findTopic(s.toString()));

        //2,为查找出的变色
        //首先要为editable,去除之前设置的colorSpan
        Editable editable = mEditText.getText();
        for (int i = 0; i < mColorSpans.size(); i++) {
            editable.removeSpan(mColorSpans.get(i));
        }
        mColorSpans.clear();
        //为editable,中的话题加入colorSpan
        int findPos = 0;
        int size = mTopicList.size();
        for (int i = 0; i < size; i++) {//便利话题
            String topic = mTopicList.get(i);
            findPos = content.indexOf(topic, findPos);
            if (findPos != -1) {
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLUE);
                editable.setSpan(colorSpan, findPos, findPos = findPos + topic.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                mColorSpans.add(colorSpan);
            }
        }
    }

    /**
     * @param s 文本
     * @return 话题集合
     */
    public static ArrayList<String> findTopic(String s) {

        Pattern p = Pattern.compile(topicRegex);
        Matcher m = p.matcher(s);

        ArrayList<String> list = new ArrayList<>();
        while (m.find()) {
            list.add(m.group());
        }

        return list;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.i("MainActivity", "beforeTextChanged");
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.i("MainActivity", "afterTextChanged");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button) {
            //插入话题
            int selectionStart = mEditText.getSelectionStart();
            String content = mEditText.getText().toString();

            String firstStr = content.substring(0, selectionStart);
            String secondStr = content.substring(selectionStart, content.length());

            String insertTopic = "#这是一个插入的话题#";
            mEditText.setText(firstStr + insertTopic + secondStr);
            mEditText.setSelection(selectionStart + insertTopic.length());
        }
        if (v.getId() == R.id.editText && mCheckBox1.isChecked()) {
            int selectionStart = mEditText.getSelectionStart();

            int lastPos = 0;
            int size = mTopicList.size();
            for (int i = 0; i < size; i++) {
                String topic = mTopicList.get(i);
                lastPos = mEditText.getText().toString().indexOf(topic, lastPos);

                if (lastPos != -1) {
                    if (selectionStart >= lastPos && selectionStart <= (lastPos + topic.length())) {
                        //在这position 区间就移动光标
                        mEditText.setSelection(lastPos + topic.length());
                    }
                }
                lastPos = lastPos + topic.length();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("MainActivity", "onKeyDown");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.i("MainActivity", "onKeyUp");
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        Log.i("MainActivity", "onKey");
        if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN && mCheckBox2.isChecked()) {

            int selectionStart = mEditText.getSelectionStart();
            int selectionEnd = mEditText.getSelectionEnd();

            if (selectionStart != selectionEnd) {
                return false;
            }

            Editable editable = mEditText.getText();
            String content = editable.toString();
            int lastPos = 0;
            int size = mTopicList.size();
            for (int i = 0; i < size; i++) {
                String topic = mTopicList.get(i);
                lastPos = content.indexOf(topic, lastPos);
                if (lastPos != -1) {
                    if (selectionStart != 0 && selectionStart >= lastPos && selectionStart <= (lastPos + topic.length())) {
                        //删除话题
                        mEditText.setSelection(lastPos, lastPos + topic.length());
                        return true;
                    }
                }
                lastPos += topic.length();
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
