package be.vlaio.dosis.connector.wip;

import be.vlaio.dosis.connector.common.CommonTestMother;
import be.vlaio.dosis.connector.common.Verwerkingsstatus;

import java.util.ArrayList;

public class WipTestMother {

    public static WorkItem.Builder someWorkItem() {
        return new WorkItem.Builder()
                .withDosisItem(CommonTestMother.someDosisItem().build())
                .withCurrentStatus(Verwerkingsstatus.TODO)
                .withTransitions(new ArrayList<>());
    }
}
