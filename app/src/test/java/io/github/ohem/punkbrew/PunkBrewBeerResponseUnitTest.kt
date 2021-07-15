package io.github.ohem.punkbrew

import io.github.ohem.punkbrew.data.api.BeerResponse
import io.github.ohem.punkbrew.data.db.BeerEntity
import junit.framework.Assert.assertEquals
import org.junit.Test


class PunkBrewBeerResponseUnitTest {

    @Test
    fun testBeerResponse() {
        val obj = BeerResponse.Value(2.0, "literes")
        val mashTemp: List<BeerResponse.MashTemp> = ArrayList<BeerResponse.MashTemp>()
        val temp: BeerResponse.Temp = BeerResponse.Temp(1, "celsius")
        val mashTempObj: BeerResponse.MashTemp = BeerResponse.MashTemp(temp, 1)
        mashTemp.toMutableList().add(mashTempObj)

        val fermentation = BeerResponse.Fermentation(temp)
        val method = BeerResponse.Method(mashTemp, fermentation, null)

        val listMalt: List<BeerResponse.Malt> = ArrayList()
        val maltValue = BeerResponse.Value(3.3, "kilograms")
        val malt: BeerResponse.Malt = BeerResponse.Malt(" Otter Extra Pale", maltValue)
        listMalt.toMutableList().add(malt)

        val listHop: List<BeerResponse.Hop> = ArrayList()
        val hopValue = BeerResponse.Hop("First Gold", maltValue, "start", "bitter")
        listHop.toMutableList().add(hopValue)
        val ingredients = BeerResponse.Ingredients(listMalt, listHop, " 1056 - American Ale™")

        val foodPairing: List<String> = ArrayList()
        foodPairing.toMutableList().add("Spicy chicken tikka masala")

        val dto = BeerResponse(
            1, "Buzz",
            "A Real Bitter Experience.",
            "09/2007",
            "A light, crisp and bitter IPA brewed with English and American hops. A small batch brewed only once.",
            "https://images.punkapi.com/v2/keg.png",
            4.5,

            6.0,
            8.0,
            2.5,
            4.3,
            3.2,
            4.0,
            5.5,
            obj,
            obj,
            method,
            ingredients,
            foodPairing,
            "The earthy and floral aromas from the hops can be overpowering. Drop a little Cascade in at the end of the boil to lift the profile with a bit of citrus.",
            "Sam Mason <samjbmason>",
        ).apply {
        }
        val model = BeerEntity(dto.id, dto.name, dto.tagline,
            dto.firstBrewed,
            dto.description,
            dto.imageUrl,
            dto.abv,
            dto.ibu,
            dto.targetFg,
            dto.targetOg,
            dto.ebc,
            dto.srm,
            dto.ph,
            dto.attenuationLevel,
            dto.brewersTips,
            dto.brewersTips,
            dto.name,
            dto.brewersTips,
            dto.description,
            dto.brewersTips,
            dto.contributedBy)
        assertEquals(1,model?.id)
        assertEquals(4.5, model?.abv)
        assertEquals("Buzz",model?.name)
    }


}


