package person.wangchen11.dbmodel;

import person.wangchen11.model.smartmodel.annotation.SmartField;
import person.wangchen11.model.smartmodel.annotation.SmartTable;
import person.wangchen11.model.smartmodelmanager.SmartManager;

@SmartTable(name="audio_history")
public class AudioHistoryTableModel {
    @SmartField
    public String addtime;
    @SmartField
    public String scantime;
    @SmartField
    public String id;

    @Override
    public String toString() {
        return SmartManager.getGson().toJson(this);
    }
}
