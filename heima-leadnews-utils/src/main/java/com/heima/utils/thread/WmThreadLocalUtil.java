package com.heima.utils.thread;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtil {
    //ThreadLocal<需要存入线程的实体类>
    private final static ThreadLocal<WmUser> WM_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 保存数据到线程ThreadLocal
     */
    public static void setUser(WmUser wmUser) {
        WM_USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取线程中的用户信息
     */
    public static WmUser getUser() {
        return WM_USER_THREAD_LOCAL.get();
    }
    /**
     * 从当前线程，获取用户对象的id
     */
    public static Integer getUserId() {
        if (WM_USER_THREAD_LOCAL.get() == null) {
            return null;
        }
        return WM_USER_THREAD_LOCAL.get().getId();
    }

    /**
     * 从当前线程，获取用户对象的其他信息
     */
    public static String getMobile() {
        if (WM_USER_THREAD_LOCAL.get() == null) {
            return null;
        }
        return WM_USER_THREAD_LOCAL.get().getPhone();
    }
    /**
     * 移除线程中数据
     */
    public static void clear() {
        WM_USER_THREAD_LOCAL.remove();
    }
}

