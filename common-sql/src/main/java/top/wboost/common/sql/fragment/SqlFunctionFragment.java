package top.wboost.common.sql.fragment;

public interface SqlFunctionFragment {

    public String toFragmentString(String alias, Object... objects);

}
