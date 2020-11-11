package person.wangchen11.dbmodel;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import java.util.List;

import person.wangchen11.model.smartmodelmanager.SmartManager;

public class MainActivity extends AppCompatActivity {
    private static  final String TAG = "MainActivity";
    private SmartManager mSmartManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String dbFile = getFilesDir().getAbsolutePath() + "/audio_history.db";
        mSmartManager = new SmartManager(SQLiteDatabase.openOrCreateDatabase(dbFile,null));
        AudioHistoryTableModel audioHistoryModel = new AudioHistoryTableModel();
        try {

            Log.i(TAG,"添加数据");
            audioHistoryModel.addtime="1111";
            audioHistoryModel.scantime="44444";
            audioHistoryModel.id="1";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="2";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="3";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="4";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="5";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="6";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="7";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="8";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="9";
            mSmartManager.insert(audioHistoryModel);

            audioHistoryModel.id="10";
            mSmartManager.insert(audioHistoryModel);

            Log.i(TAG,"所有数据:"+mSmartManager.queryObjects(AudioHistoryTableModel.class));

             mSmartManager.delete(AudioHistoryTableModel.class,"id=?",new String[]{"6"});
            Log.i(TAG,"删除id 为6后的数据:"+mSmartManager.queryObjects(AudioHistoryTableModel.class,null,null,"id desc",null,0,-1));

            mSmartManager.delete(AudioHistoryTableModel.class,"id=?",new String[]{"10"});
            Log.i(TAG,"删除id 为10后的数据:"+mSmartManager.queryObjects(AudioHistoryTableModel.class,null,null,"id",null,0,-1));

            Log.i(TAG,"清空表格");
            mSmartManager.drop(AudioHistoryTableModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        mSmartManager.destory();
        super.onDestroy();
    }


}


