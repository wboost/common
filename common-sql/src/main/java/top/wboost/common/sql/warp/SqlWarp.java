package top.wboost.common.sql.warp;

import top.wboost.common.base.interfaces.FieldWarpper;
import top.wboost.common.base.interfaces.Warpper;

public interface SqlWarp extends FieldWarpper<Object,String> {

    public String[] warp(Object[] val);

    public static SqlWarp defaultSqlWarp = new DefaultSqlWarp();

}
