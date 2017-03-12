package org.limlee.hipraiseanimationlib.base;

public interface IPraiseView {

    /**
     * 添加一个点赞的对象，这个对象用来描述这个点赞的动画，但它并不是一个动画对象
     *
     * @param praise
     */
    void addPraise(IPraise praise);

}
