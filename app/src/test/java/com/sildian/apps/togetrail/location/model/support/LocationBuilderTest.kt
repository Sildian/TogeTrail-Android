package com.sildian.apps.togetrail.location.model.support

import com.google.android.libraries.places.api.model.AddressComponent
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
import org.junit.Test
import org.junit.Before
import org.junit.Assert.*
import org.mockito.Mockito

class LocationBuilderTest {

    /*********************************Main fields***********************************************/

    private lateinit var addressComponent1: AddressComponent
    private lateinit var addressComponent2a: AddressComponent
    private lateinit var addressComponent2b: AddressComponent
    private lateinit var addressComponent2c: AddressComponent
    private lateinit var addressComponent2d: AddressComponent
    private lateinit var place1:Place
    private lateinit var place2:Place
    private lateinit var place3:Place
    private lateinit var place4:Place

    /**********************************Init the main fields************************************/

    @Before
    fun init(){
        initAddressComponents()
        initPlace1()
        initPlace2()
        initPlace3()
        initPlace4()
    }

    private fun initAddressComponents(){
        this.addressComponent1=Mockito.mock(AddressComponent::class.java)
        Mockito.`when`(this.addressComponent1.types).thenReturn(listOf("country"))
        Mockito.`when`(this.addressComponent1.shortName).thenReturn("FR")
        Mockito.`when`(this.addressComponent1.name).thenReturn("France")
        this.addressComponent2a=Mockito.mock(AddressComponent::class.java)
        Mockito.`when`(this.addressComponent2a.types).thenReturn(listOf("administrative_area_level_1"))
        Mockito.`when`(this.addressComponent2a.shortName).thenReturn("IDF")
        Mockito.`when`(this.addressComponent2a.name).thenReturn("Ile de France")
        this.addressComponent2b=Mockito.mock(AddressComponent::class.java)
        Mockito.`when`(this.addressComponent2b.types).thenReturn(listOf("administrative_area_level_1"))
        Mockito.`when`(this.addressComponent2b.shortName).thenReturn("Normandie")
        Mockito.`when`(this.addressComponent2b.name).thenReturn("Normandie")
        this.addressComponent2c=Mockito.mock(AddressComponent::class.java)
        Mockito.`when`(this.addressComponent2c.types).thenReturn(listOf("administrative_area_level_1"))
        Mockito.`when`(this.addressComponent2c.shortName).thenReturn("Auvergne-Rhones-Alpes")
        Mockito.`when`(this.addressComponent2c.name).thenReturn("Auvergne-Rhones-Alpes")
        this.addressComponent2d=Mockito.mock(AddressComponent::class.java)
        Mockito.`when`(this.addressComponent2d.types).thenReturn(listOf("administrative_area_level_1"))
        Mockito.`when`(this.addressComponent2d.shortName).thenReturn("Pays de la Loire")
        Mockito.`when`(this.addressComponent2d.name).thenReturn("Pays de la Loire")
    }

    private fun initPlace1(){
        val addressComponents=Mockito.mock(AddressComponents::class.java)
        Mockito.`when`(addressComponents.asList())
            .thenReturn(listOf(this.addressComponent1, this.addressComponent2a))
        this.place1= Mockito.mock(Place::class.java)
        Mockito.`when`(this.place1.address).thenReturn("1 rue de Paris, Paris, Ile de France, France")
        Mockito.`when`(this.place1.addressComponents).thenReturn(addressComponents)
    }

    private fun initPlace2(){
        val addressComponents=Mockito.mock(AddressComponents::class.java)
        Mockito.`when`(addressComponents.asList())
            .thenReturn(listOf(this.addressComponent1, this.addressComponent2b))
        this.place2= Mockito.mock(Place::class.java)
        Mockito.`when`(this.place2.address).thenReturn("1 rue de Normandie, Rouen, Normandie, France")
        Mockito.`when`(this.place2.addressComponents).thenReturn(addressComponents)
    }

    private fun initPlace3(){
        val addressComponents=Mockito.mock(AddressComponents::class.java)
        Mockito.`when`(addressComponents.asList())
            .thenReturn(listOf(this.addressComponent1, this.addressComponent2c))
        this.place3= Mockito.mock(Place::class.java)
        Mockito.`when`(this.place3.address).thenReturn("1 rue de la Montagne, Chamonix, Savoie, France")
        Mockito.`when`(this.place3.addressComponents).thenReturn(addressComponents)
    }

    private fun initPlace4(){
        val addressComponents=Mockito.mock(AddressComponents::class.java)
        Mockito.`when`(addressComponents.asList())
            .thenReturn(listOf(this.addressComponent1, this.addressComponent2d))
        this.place4= Mockito.mock(Place::class.java)
        Mockito.`when`(this.place4.address).thenReturn("1 rue de la Loire, Tour, Loire, France")
        Mockito.`when`(this.place4.addressComponents).thenReturn(addressComponents)
    }

    /******************************************Test*********************************************/

    @Test
    fun given_place1_when_buildWithPlace_then_checkResult() {
        val location=LocationBuilder()
            .withPlace(this.place1)
            .build()
        assertEquals("FR", location.country?.code)
        assertEquals("IDF", location.region?.code)
        assertEquals("1 rue de Paris, Paris, Ile de France, France", location.fullAddress)
    }

    @Test
    fun given_place2_when_buildWithPlace_then_checkResult() {
        val location=LocationBuilder()
            .withPlace(this.place2)
            .build()
        assertEquals("FR", location.country?.code)
        assertEquals("NO", location.region?.code)
        assertEquals("1 rue de Normandie, Rouen, Normandie, France", location.fullAddress)
    }

    @Test
    fun given_place3_when_buildWithPlace_then_checkResult() {
        val location=LocationBuilder()
            .withPlace(this.place3)
            .build()
        assertEquals("FR", location.country?.code)
        assertEquals("ARA", location.region?.code)
        assertEquals("1 rue de la Montagne, Chamonix, Savoie, France", location.fullAddress)
    }

    @Test
    fun given_place4_when_buildWithPlace_then_checkResult() {
        val location=LocationBuilder()
            .withPlace(this.place4)
            .build()
        assertEquals("FR", location.country?.code)
        assertEquals("PDLL", location.region?.code)
        assertEquals("1 rue de la Loire, Tour, Loire, France", location.fullAddress)
    }
}