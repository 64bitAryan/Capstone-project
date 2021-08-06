package com.project.findme.utils

import androidx.lifecycle.Observer

val <T> T.exhaustive: T
    get() = this

class Events<out T>(private val content: T) {
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T?{
        return if(!hasBeenHandled){
            hasBeenHandled = true
            content
        } else null
    }

    fun peekContent() = content
}

class EventObserver<T>(
    private inline val onError: ((String) -> Unit)? = null,
    private inline val onLoading: (() -> Unit)? = null,
    private inline val onSuccess: (T) -> Unit
): Observer<Events<Resource<T>>>{
    override fun onChanged(t: Events<Resource<T>>?) {
        when(val content = t?.peekContent()){
            is Resource.Success -> {
                content.data?.let(onSuccess)
            }
            is Resource.Error -> {
                t.getContentIfNotHandled()?.let{
                    onError?.let { error ->
                        error(it.message!!)
                    }
                }
            }
            is Resource.Loading -> {
                onLoading?.let { loading ->
                    loading()
                }
            }
        }
    }
}