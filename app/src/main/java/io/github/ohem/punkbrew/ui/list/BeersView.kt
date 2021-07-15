package io.github.ohem.punkbrew.ui.list

import io.github.ohem.punkbrew.data.db.BeerEntity
import io.github.ohem.punkbrew.ui.base.BaseView
import io.github.ohem.punkbrew.ui.base.BaseViewModel

interface BeersView<VM : BeersViewModel> : BaseView<BaseViewModel> {

    fun onBeerClicked(beer: BeerEntity)

    fun onBeerUpdated()
}
