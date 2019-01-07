package top.wboost.common.base.entity;

import top.wboost.common.system.code.SystemCode;

/**
 * ResultEntity简化类
 * @Auther: jwsun
 * @Date: 2018/12/24 10:19
 */
public class Rt {

    public static RtBuilder s(int code) {
        return new RtBuilder(code,true);
    }

    public static RtBuilder s(SystemCode code) {
        return new RtBuilder(code.getCode(),true);
    }

    public static RtBuilder f(int code) {
        return new RtBuilder(code,false);
    }

    public static RtBuilder f(SystemCode code) {
        return new RtBuilder(code.getCode(),false);
    }

    public static class RtBuilder {

        int code;
        SystemCode syscode;
        boolean success;
        Object data;
        String[] filterNames;

        public RtBuilder(int code,boolean success) {
            this.code = code;
            this.success = success;
        }

        public RtBuilder(SystemCode code, boolean success) {
            this.syscode = code;
            this.success = success;
        }

        public RtBuilder d(Object data) {
            this.data = data;
            return this;
        }

        public RtBuilder f(String... filterNames) {
            this.filterNames = filterNames;
            return this;
        }

        public ResultEntity build() {
            ResultEntity.ResultBodyBuilder builder = null;
            if (success)
                if (this.syscode == null)
                    builder = ResultEntity.success(this.code);
                else
                    builder = ResultEntity.success(this.syscode);
            else
                if (this.syscode == null)
                    builder = ResultEntity.fail(this.code);
                else
                    builder = ResultEntity.fail(this.syscode);

            return builder.setData(this.data).setFilterNames(filterNames).build();
        }

    }

}
