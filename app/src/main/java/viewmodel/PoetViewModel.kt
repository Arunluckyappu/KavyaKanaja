package com.kavyakanaja.app.viewmodel

import androidx.lifecycle.ViewModel
import com.kavyakanaja.app.data.repository.PoemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PoetViewModel @Inject constructor(
    private val repository: PoemRepository
) : ViewModel() {
    fun getPoetById(id: Int) = repository.getPoetById(id)
    fun getPoemsByPoet(poetId: Int) = repository.getPoemsByPoet(poetId)
}
