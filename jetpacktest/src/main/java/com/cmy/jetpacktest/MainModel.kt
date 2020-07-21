package com.cmy.jetpacktest

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel

/**
 *   Created by chenmy on 2020/7/14.
 */
class MainModel(countReserved: Int) : ViewModel() {
    //封装用户 只提供给姓名 不提供年龄给外部
    private val userLiveData = MutableLiveData<User>()

    val userName: LiveData<String> = Transformations.map(userLiveData) { user ->
        "${user.firstName} ${user.lastName}"
    }

    //不暴露给外部
    private val _counter = MutableLiveData<Int>()

    val counter: LiveData<Int>
        get() = _counter

    init {
        _counter.value = countReserved
    }

    fun plusOne() {
        val count = _counter.value ?: 0
        _counter.value = count + 1
    }

    fun clear() {
        _counter.value = 0
    }

    private val userIdLiveData = MutableLiveData<String>()

    //数据不再viewmode中创建livedata 不能使用上述 map方法 直接调用的话每次都是新的对象
    //从而无法观察到数据变 switchMap 将这个liveData转化成一个新的可观察的liveData
    val user: LiveData<User> = Transformations.switchMap(userIdLiveData) { aa ->
        Repository.getUser(aa)
    }

    fun getUser(userId: String) {
        userIdLiveData.value = userId
    }
}