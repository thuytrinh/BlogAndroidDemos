package thuytrinh.hotelsapp

import androidx.navigation.NavController
import org.koin.dsl.module

object HotelListNavigatorImpl : HotelListNavigator {
  override fun navigateToHotelDetails(navController: NavController) {
    TODO("Not implemented yet")
  }
}

object HotelDetailsNavigatorImpl : HotelDetailsNavigator {
  override fun navigateToBooking(navController: NavController) {
    TODO("Not implemented yet")
  }
}

val navModule = module {
  single<HotelListNavigator> { HotelListNavigatorImpl }
  single<HotelDetailsNavigator> { HotelDetailsNavigatorImpl }
}
