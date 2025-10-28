package com.softwinner.dvr.dvr;



public interface RecordListener {

    public void onInitFinished();

    public void onStartRecord();

    public void onRecordFailed(String reason);

    public void onStopRecord();

    public void onNextRecord();

    public void onFreshTime(String time);
}
