package io.github.ohem.punkbrew.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import io.github.ohem.punkbrew.data.BeersRepository
import io.github.ohem.punkbrew.data.db.BeerEntity
import io.github.ohem.punkbrew.ui.base.BaseViewModel
import io.reactivex.Single

class DetailsViewModel(
    private val repository: BeersRepository
) : BaseViewModel() {

    internal var currentBeer: BeerEntity? = null

    private val idLiveData = MutableLiveData<Int>()

    val beer : LiveData<Single<BeerEntity>> = Transformations.map(idLiveData) {
        repository.beer(it)
    }

    fun findBeer(beerId: Int) {
        idLiveData.postValue(beerId)
    }

}
