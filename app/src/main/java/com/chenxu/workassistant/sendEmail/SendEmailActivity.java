package com.chenxu.workassistant.sendEmail;

import android.graphics.Color;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.SeekBar;

import com.chenxu.workassistant.BaseActivity;
import com.chenxu.workassistant.R;
import com.chenxu.workassistant.databinding.ActivitySendEmailBinding;
import com.chenxu.workassistant.utils.ConfirmDialog;
import com.chenxu.workassistant.utils.DialogUtil;
import com.chenxu.workassistant.utils.SnackBarUtils;
import com.chenxu.workassistant.utils.StatusBarUtil;
import com.chenxu.workassistant.utils.Utils;
import com.chenxu.workassistant.widget.ColorPicker;
import com.chenxu.workassistant.widget.KeyboardChangeListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SendEmailActivity extends BaseActivity<ActivitySendEmailBinding> implements SendEmailContract.View, View.OnClickListener {

    private SendEmailContract.Presenter mPresenter;
    private EnclosureAdapter adapter;

    private PopupWindow sendLoadDialog;

    private int textSize = 3;
    private int textColor = Color.parseColor("#000000"), bgColor = Color.parseColor("#FFFFFF");
    private boolean isKey = false;
    private int colorType = 0; // 0其他 1字体 2背景

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_email;
    }

    @Override
    protected void initView() {
        StatusBarUtil.darkMode(this);
        this.mPresenter = new SendEmailPresenter(this, this);
        mBinding.ivSendEnclosureState.setTag(R.drawable.send_email_down);
        mBinding.rvEnclosure.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration decoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        decoration.setDrawable(this.getResources().getDrawable(R.drawable.file_divider));
        mBinding.rvEnclosure.addItemDecoration(decoration);
        adapter = new EnclosureAdapter(new ArrayList<>(), this);
        mBinding.rvEnclosure.setAdapter(adapter);
        mBinding.vFormatColor.setBackgroundColor(textColor);
        mBinding.vFormatBgcolor.setBackgroundColor(bgColor);
        mBinding.tvFormatSize.setText(textSize+"");
        mBinding.reContent.setFontSize(textSize);
        mBinding.reContent.setPlaceholder(this.getResources().getString(R.string.send_email_hint));
        mPresenter.start();
    }

    @Override
    protected void bindEvent() {
        mBinding.btnSend.setOnClickListener(this::onClick);
        mBinding.btnBack.setOnClickListener(this::onClick);
        mBinding.rlEnclosureTitle.setOnClickListener(this::onClick);
        mBinding.btnFormatUndo.setOnClickListener(this::onClick);
        mBinding.btnFormatRedo.setOnClickListener(this::onClick);
        mBinding.llFormatSize.setOnClickListener(this::onClick);
        mBinding.btnFormatBold.setOnClickListener(this::onClick);
        mBinding.btnFormatItalic.setOnClickListener(this::onClick);
        mBinding.llFormatColor.setOnClickListener(this::onClick);
        mBinding.llFormatBgcolor.setOnClickListener(this::onClick);
        mBinding.btnFormatH1.setOnClickListener(this::onClick);
        mBinding.btnFormatH2.setOnClickListener(this::onClick);
        mBinding.btnFormatH3.setOnClickListener(this::onClick);
        mBinding.btnFormatH4.setOnClickListener(this::onClick);
        mBinding.btnFormatH5.setOnClickListener(this::onClick);
        mBinding.btnFormatH6.setOnClickListener(this::onClick);
        mBinding.btnFormatUnderline.setOnClickListener(this::onClick);
        mBinding.btnFormatStrikethrough.setOnClickListener(this::onClick);
        mBinding.btnFormatSubscript.setOnClickListener(this::onClick);
        mBinding.btnFormatSuperscript.setOnClickListener(this::onClick);
        mBinding.btnFormatLeft.setOnClickListener(this::onClick);
        mBinding.btnFormatCenter.setOnClickListener(this::onClick);
        mBinding.btnFormatRight.setOnClickListener(this::onClick);
        mBinding.btnFormatIndent.setOnClickListener(this::onClick);
        mBinding.btnFormatOutdent.setOnClickListener(this::onClick);
        mBinding.btnFormatBullets.setOnClickListener(this::onClick);
        mBinding.btnFormatNumbers.setOnClickListener(this::onClick);
        mBinding.btnSend.setOnClickListener(this::onClick);

        mBinding.sbSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean fromUser) {
                if (fromUser){
                    textSize = i;
                    mBinding.tvFormatSize.setText(textSize+"");
                    mBinding.reContent.setFontSize(textSize);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mBinding.reContent.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b && isKey) {
                    initTextFormat();
                }
            }
        });
        new KeyboardChangeListener(this).setKeyBoardListener(new KeyboardChangeListener.KeyBoardListener() {
            @Override
            public void onKeyboardChange(boolean isShow, int keyboardHeight) {
                isKey = isShow;
                if (isShow) {
                    if (mBinding.reContent.isFocused()) {
                        initTextFormat();
                    }
                }
            }
        });

        adapter.setOnItemButtonClickListener(new EnclosureAdapter.OnItemButtonClickListener() {
            @Override
            public void onItemButtonClick(int position) {
                //删除附件
                Log.e("onItemButtonClick", position + "");
                mPresenter.deleteEnclosure(position);
            }
        });

    }

    @Override
    public void onClick(View view) {
        colorType = 0;
        switch (view.getId()) {
            case R.id.btn_send:
                sendEmail();
                break;
            case R.id.btn_back:
                onBackPressed();
                break;
            case R.id.rl_enclosure_title:
                mPresenter.enclosureTitleClick();
                break;
            case R.id.btn_format_undo:
                mBinding.reContent.undo();
                break;
            case R.id.btn_format_redo:
                mBinding.reContent.redo();
                break;
            case R.id.ll_format_size:
                toggleSizeSeekBar();
                break;
            case R.id.btn_format_bold:
                mBinding.reContent.setBold();
                break;
            case R.id.btn_format_italic:
                mBinding.reContent.setItalic();
                break;
            case R.id.ll_format_color:
                colorType = 1;
                setTextColor();
                break;
            case R.id.ll_format_bgcolor:
                colorType = 2;
                setBgColor();
                break;
            case R.id.btn_format_h1:
                mBinding.reContent.setHeading(1);
                break;
            case R.id.btn_format_h2:
                mBinding.reContent.setHeading(2);
                break;
            case R.id.btn_format_h3:
                mBinding.reContent.setHeading(3);
                break;
            case R.id.btn_format_h4:
                mBinding.reContent.setHeading(4);
                break;
            case R.id.btn_format_h5:
                mBinding.reContent.setHeading(5);
                break;
            case R.id.btn_format_h6:
                mBinding.reContent.setHeading(6);
                break;
            case R.id.btn_format_underline:
                mBinding.reContent.setUnderline();
                break;
            case R.id.btn_format_strikethrough:
                mBinding.reContent.setStrikeThrough();
                break;
            case R.id.btn_format_subscript:
                mBinding.reContent.setSubscript();
                break;
            case R.id.btn_format_superscript:
                mBinding.reContent.setSuperscript();
                break;
            case R.id.btn_format_left:
                mBinding.reContent.setAlignLeft();
                break;
            case R.id.btn_format_center:
                mBinding.reContent.setAlignCenter();
                break;
            case R.id.btn_format_right:
                mBinding.reContent.setAlignRight();
                break;
            case R.id.btn_format_indent:
                mBinding.reContent.setIndent();
                break;
            case R.id.btn_format_outdent:
                mBinding.reContent.setOutdent();
                break;
            case R.id.btn_format_bullets:
                mBinding.reContent.setBullets();
                break;
            case R.id.btn_format_numbers:
                mBinding.reContent.setNumbers();
                break;
        }
    }

    private void setTextColor() {
        Utils.closeKeyboard(this);
        ColorPicker.with(this)
                .setTitle(R.string.send_email_format_text_color)
                .setStartColor(textColor)
                .setDensity(14)
                .setRelyView(mBinding.rlBar)
                .setColorSelectedListener(new ColorPicker.Listener.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectColor) {
                        textColor = selectColor;
                        mBinding.vFormatColor.setBackgroundColor(textColor);
                    }
                })
                .setDialogCancelListener(new ColorPicker.Listener.OnDialogCancelListener() {
                    @Override
                    public void onDialogCancel() {

                    }
                })
                .build()
                .show();
    }

    private void setBgColor() {
        Utils.closeKeyboard(this);
        ColorPicker.with(this)
                .setTitle(R.string.send_email_format_background_color)
                .setStartColor(bgColor)
                .setDensity(14)
                .setRelyView(mBinding.rlBar)
                .setColorSelectedListener(new ColorPicker.Listener.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectColor) {
                        bgColor = selectColor;
                        mBinding.vFormatBgcolor.setBackgroundColor(bgColor);
                    }
                })
                .setDialogCancelListener(new ColorPicker.Listener.OnDialogCancelListener() {
                    @Override
                    public void onDialogCancel() {

                    }
                })
                .build()
                .show();
    }

    private void initTextFormat() {
        switch (colorType) {
            case 1:
                mBinding.reContent.setTextColor(textColor);
                break;
            case 2:
                mBinding.reContent.setTextBackgroundColor(bgColor);
                break;
        }
        mBinding.reContent.setFontSize(textSize);
        if (mBinding.rlEnclosure.getVisibility() == View.VISIBLE) {
            toggleEnclosureIconAndShowEnclosure();
        }
    }

    @Override
    public void setEnclosureDetail(int text) {
        mBinding.tvEnclosureDetail.setText(text);
    }

    @Override
    public void setEnclosureDetail(String text) {
        mBinding.tvEnclosureDetail.setText(text);
    }

    @Override
    public void setEnclosureData(List<File> files) {
        adapter.setData(files);
    }

    @Override
    public void toggleEnclosureIcon() {
        int iconID = (int) mBinding.ivSendEnclosureState.getTag();
        switch (iconID) {
            case R.drawable.send_email_down:
                mBinding.ivSendEnclosureState.setImageResource(R.drawable.send_email_up);
                mBinding.ivSendEnclosureState.setTag(R.drawable.send_email_up);
                break;
            case R.drawable.send_email_up:
                mBinding.ivSendEnclosureState.setImageResource(R.drawable.send_email_down);
                mBinding.ivSendEnclosureState.setTag(R.drawable.send_email_down);
                break;
        }
    }

    @Override
    public void toggleEnclosureIconAndShowEnclosure() {
        int iconID = (int) mBinding.ivSendEnclosureState.getTag();
        switch (iconID) {
            case R.drawable.send_email_down:
                Utils.closeKeyboard(this);
                mBinding.ivSendEnclosureState.setImageResource(R.drawable.send_email_up);
                mBinding.ivSendEnclosureState.setTag(R.drawable.send_email_up);
                mBinding.rlEnclosure.setVisibility(View.VISIBLE);
                break;
            case R.drawable.send_email_up:
                mBinding.ivSendEnclosureState.setImageResource(R.drawable.send_email_down);
                mBinding.ivSendEnclosureState.setTag(R.drawable.send_email_down);
                mBinding.rlEnclosure.setVisibility(View.GONE);
                break;
        }
    }

    private void toggleSizeSeekBar(){
        if (mBinding.rlSize.getVisibility() == View.GONE){
            mBinding.rlSize.setVisibility(View.VISIBLE);
        }else {
            mBinding.rlSize.setVisibility(View.GONE);
        }
    }

    @Override
    public void removeAdopterItem(int position) {
        adapter.removeItem(position);
    }

    @Override
    public void sendEmail() {
        String reader = mBinding.etSendReader.getText().toString().trim();
        String title = mBinding.etSendTitle.getText().toString().trim();
        String content = mBinding.reContent.getHtml();
        mPresenter.checkEmailInfo(reader, title, content);
    }

    @Override
    public void showErrorSnackBar(int text) {
        Utils.closeKeyboard(this);
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar, text, R.color.white, R.color.red);
    }

    @Override
    public void showSnackBar(int text) {
        Utils.closeKeyboard(this);
        SnackBarUtils.showSnackBarMSG(mBinding.rlBar, text, R.color.white, R.color.mainBlue);
    }

    @Override
    public void showConfirmDialog(String reader, String title, String content) {
        Utils.closeKeyboard(this);
        ConfirmDialog.with(this)
                .setHint(R.string.send_email_big_enclosure_hint)
                .setRelyView(mBinding.rlBar)
                .setOnCancelListener(new ConfirmDialog.ConfirmListener.OnDialogCancelClickListener() {
                    @Override
                    public void onCancelButtonClick() {

                    }
                })
                .setOnConfirmListener(new ConfirmDialog.ConfirmListener.OnDialogConfirmClickListener() {
                    @Override
                    public void onConfirmButtonClick() {
                        mPresenter.sendEmail(reader, title, content);
                    }
                })
                .build()
                .show();
    }

    @Override
    public void showLoadDialog() {
        Utils.closeKeyboard(this);
        sendLoadDialog = DialogUtil.initSendLoadDialog(this);
        sendLoadDialog.showAtLocation(mBinding.rlBar, Gravity.CENTER, 0, 0);
    }

    @Override
    public void cancelLoadDialog() {
        if (sendLoadDialog != null) {
            sendLoadDialog.dismiss();
        }
    }

    @Override
    public void sendEmailSuccess() {
        mBinding.etSendTitle.setText("");
        mBinding.reContent.setHtml("");
        showSnackBar(R.string.send_email_200);
    }

    @Override
    public void sendEmailFinal() {
        showErrorSnackBar(R.string.send_email_err400);
    }
}
