package com.softwinner.dvr.ui.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSpinner;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.softwinner.dvr.R;
import com.softwinner.dvr.common.DVRApplication;
import com.softwinner.dvr.common.DVRPreference;
import com.softwinner.dvr.permission.PermissionsManager;
import com.softwinner.dvr.permission.PermissionsResultAction;
import com.softwinner.dvr.util.Logger;
import com.softwinner.dvr.util.StorageUtils;

public class ChoiceCameraActivity extends BaseActivity {
    private static final String TAG = "ChoiceCameraActivity";
    private AppCompatSpinner mChoiceSpinner;
    private Spinner mFrontSpinner;
    private Spinner mReverseSpinner;
    private Spinner mRightSpinner;
    private Spinner mLeftSpinner;

    private ArrayAdapter mFrontAdapter;
    private ArrayAdapter mReverseAdapter;
    private ArrayAdapter mRightAdapter;
    private ArrayAdapter mLeftAdapter;

    private ProgressDialog mFormatProgressDialog;

    private Handler mFormatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (mFormatProgressDialog != null) {
                        mFormatProgressDialog.dismiss();
                    }
                    Intent intent1 = new Intent(ChoiceCameraActivity.this, RecordActivity.class);
                    startActivity(intent1);
                    finish();
                    break;
                case -1:
                    if (mFormatProgressDialog != null) {
                        mFormatProgressDialog.dismiss();
                    }

                    Toast.makeText(ChoiceCameraActivity.this, R.string.format_failed, Toast
                            .LENGTH_LONG).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppStatusManager.getInstance().setAppStatus(AppStatusManager.AppStatusConstant.APP_NORMAL);
        super.onCreate(savedInstanceState);

        boolean result = PermissionsManager.getInstance().hasPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (!result) {
            PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, new
                    String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new
                    PermissionsResultAction() {
                @Override
                public void onGranted() {

                }

                @Override
                public void onDenied(String permission) {

                }
            });
        }

        if (DVRPreference.getInstance(this).isFirstStart()) {
            DVRApplication.getInstance().reset();
            //setContentView(R.layout.activity_choice_camera);
            //initView();
            DVRPreference.getInstance(getApplicationContext()).setDVRType(5);
            Intent intent2 = new Intent(this, RecordActivity.class);
            startActivity(intent2);
            finish();

        } else {
            Intent intent2 = new Intent(this, RecordActivity.class);
            startActivity(intent2);
            finish();
        }
    }

    private void initView() {
        mChoiceSpinner = (AppCompatSpinner) findViewById(R.id.choice_s);
        mChoiceSpinner.setSelection(DVRPreference.getInstance(this).getDVRType());
        mFrontSpinner = (Spinner) findViewById(R.id.front_s);
        mFrontSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = Integer.parseInt(mFrontAdapter.getItem(position).toString());
                DVRPreference.getInstance(getApplicationContext()).setFrontCameraId(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mReverseSpinner = (Spinner) findViewById(R.id.reverse_s);
        mReverseSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = Integer.parseInt(mReverseAdapter.getItem(position).toString());
                DVRPreference.getInstance(getApplicationContext()).setReverseCameraId(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mRightSpinner = (Spinner) findViewById(R.id.right_s);
        mRightSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = Integer.parseInt(mRightAdapter.getItem(position).toString());
                DVRPreference.getInstance(getApplicationContext()).setRightCameraId(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLeftSpinner = (Spinner) findViewById(R.id.left_s);
        mLeftSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index = Integer.parseInt(mLeftAdapter.getItem(position).toString());
                DVRPreference.getInstance(getApplicationContext()).setLeftCameraId(index);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mChoiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDVRType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        findViewById(R.id.ok_b).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchDVR();
            }
        });
    }


    private void switchDVR() {
        //格式化sdCard
        //1.card是否存在
        if (StorageUtils.haveExtraSdcard(getApplicationContext())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.format_sdcard_alert);
            builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    formatSdcard();
                }
            });
            builder.create().show();
        } else {
            Intent intent3 = new Intent(ChoiceCameraActivity.this, RecordActivity.class);
            startActivity(intent3);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.i(TAG,"---onStart");
    }


    private void formatSdcard() {
        if (mFormatProgressDialog == null) {
            mFormatProgressDialog = new ProgressDialog(this);
            mFormatProgressDialog.setCanceledOnTouchOutside(false);
            mFormatProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        mFormatProgressDialog.setMessage(getString(R.string.format_sdcard));
        mFormatProgressDialog.show(this, getString(R.string.format_sdcard), getString(R.string
                .please_wait));
        new Thread() {
            @Override
            public void run() {
                String[] paths = StorageUtils.getStoragePaths(getApplicationContext());
                for (String path : paths) {
                    if (path.contains("card")) {
                        mFormatHandler.sendEmptyMessageDelayed(-1, 60000);
                        StorageUtils.diskFormat(getApplicationContext(), path);
                        mFormatHandler.removeMessages(-1);
                        mFormatHandler.sendEmptyMessage(0);
                    }
                }
                mFormatHandler.sendEmptyMessage(0);
            }
        }.start();

    }

    private void selectedDVRType(int type) {
        DVRPreference.getInstance(getApplicationContext()).setDVRType(type);
        switch (type) {
            case DVRPreference.DVR_TYPE_SINGLE_CAMERA:
                oneCamera();
                break;
            case DVRPreference.DVR_TYPE_TWO_CAMERA:
                twoCamera();
                break;
            case DVRPreference.DVR_TYPE_FOUR_CVBS:
                fourCvbsCamera();
                break;
            case DVRPreference.DVR_TYPE_FOUR_CSI:
                fourCsiCamera();
                break;
            case DVRPreference.DVR_TYPE_CVBS_FOUR_TO_ONE:
                four2OneCvbsCamera();
                break;
            case DVRPreference.DVR_TYPE_CSI_FOUR_TO_ONE:
                four2OneCsiCamera();
                break;
            default:
                oneCamera();
        }
    }

    private void oneCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.all_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.GONE);
        mLeftSpinner.setVisibility(View.GONE);
        mRightSpinner.setVisibility(View.GONE);
        mFrontSpinner.setEnabled(true);
    }

    private void twoCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.all_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mReverseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.all_ids));
        mReverseSpinner.setAdapter(mReverseAdapter);
        mReverseSpinner.setSelection(1);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.VISIBLE);
        mLeftSpinner.setVisibility(View.GONE);
        mRightSpinner.setVisibility(View.GONE);


        mFrontSpinner.setEnabled(true);
        mReverseSpinner.setEnabled(true);
    }

    private void four2OneCvbsCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.cvbs_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mReverseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.cvbs_ids));
        mReverseSpinner.setAdapter(mReverseAdapter);
        mLeftAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources()
                .getStringArray(R.array.cvbs_ids));
        mLeftSpinner.setAdapter(mLeftAdapter);
        mRightAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.cvbs_ids));
        mRightSpinner.setAdapter(mRightAdapter);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.VISIBLE);
        mLeftSpinner.setVisibility(View.VISIBLE);
        mRightSpinner.setVisibility(View.VISIBLE);

        mFrontSpinner.setSelection(0);
        mReverseSpinner.setSelection(1);
        mLeftSpinner.setSelection(2);
        mRightSpinner.setSelection(3);
        mFrontSpinner.setEnabled(false);
        mReverseSpinner.setEnabled(false);
        mLeftSpinner.setEnabled(false);
        mRightSpinner.setEnabled(false);
    }

    private void four2OneCsiCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.csi_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mReverseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.csi_ids));
        mReverseSpinner.setAdapter(mReverseAdapter);
        mLeftAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources()
                .getStringArray(R.array.csi_ids));
        mLeftSpinner.setAdapter(mLeftAdapter);
        mRightAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.csi_ids));
        mRightSpinner.setAdapter(mRightAdapter);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.VISIBLE);
        mLeftSpinner.setVisibility(View.VISIBLE);
        mRightSpinner.setVisibility(View.VISIBLE);

        mFrontSpinner.setSelection(0);
        mReverseSpinner.setSelection(1);
        mLeftSpinner.setSelection(2);
        mRightSpinner.setSelection(3);
        mFrontSpinner.setEnabled(false);
        mReverseSpinner.setEnabled(false);
        mLeftSpinner.setEnabled(false);
        mRightSpinner.setEnabled(false);
    }

    private void fourCvbsCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.cvbs_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mReverseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.cvbs_ids));
        mReverseSpinner.setAdapter(mReverseAdapter);
        mLeftAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources()
                .getStringArray(R.array.cvbs_ids));
        mLeftSpinner.setAdapter(mLeftAdapter);
        mRightAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.cvbs_ids));
        mRightSpinner.setAdapter(mRightAdapter);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.VISIBLE);
        mLeftSpinner.setVisibility(View.VISIBLE);
        mRightSpinner.setVisibility(View.VISIBLE);

        mFrontSpinner.setSelection(0);
        mReverseSpinner.setSelection(1);
        mLeftSpinner.setSelection(2);
        mRightSpinner.setSelection(3);
    }

    private void fourCsiCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.csi_ids));
        mFrontSpinner.setAdapter(mFrontAdapter);
        mReverseAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.csi_ids));
        mReverseSpinner.setAdapter(mReverseAdapter);
        mLeftAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources()
                .getStringArray(R.array.csi_ids));
        mLeftSpinner.setAdapter(mLeftAdapter);
        mRightAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, getResources
                ().getStringArray(R.array.csi_ids));
        mRightSpinner.setAdapter(mRightAdapter);
        mFrontSpinner.setVisibility(View.VISIBLE);
        mReverseSpinner.setVisibility(View.VISIBLE);
        mLeftSpinner.setVisibility(View.VISIBLE);
        mRightSpinner.setVisibility(View.VISIBLE);

        mFrontSpinner.setSelection(0);
        mReverseSpinner.setSelection(1);
        mLeftSpinner.setSelection(2);
        mRightSpinner.setSelection(3);
    }

    private void oneUsbCamera() {
        mFrontAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, new
                String[]{"10"});
        mRightSpinner.setVisibility(View.GONE);
        mLeftSpinner.setVisibility(View.GONE);
        mReverseSpinner.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        Logger.i(TAG, "---onDestroy");
        DVRPreference.getInstance(this).setFirstStart(false);
        super.onDestroy();
    }
}
