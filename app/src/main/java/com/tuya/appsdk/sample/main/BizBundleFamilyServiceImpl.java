package com.tuya.appsdk.sample.main;

import com.tuya.smart.commonbiz.bizbundle.family.api.AbsBizBundleFamilyService;

public class BizBundleFamilyServiceImpl extends AbsBizBundleFamilyService {

    private long mHomeId;
    private String mHomeName;

    @Override
    public long getCurrentHomeId() {
        return mHomeId;
    }

    @Override
    public void shiftCurrentFamily(long familyId, String curName) {
        super.shiftCurrentFamily(familyId, curName);
        mHomeId = familyId;
        mHomeName = curName;
    }
}
