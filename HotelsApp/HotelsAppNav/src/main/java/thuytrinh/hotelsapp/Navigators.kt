package thuytrinh.hotelsapp

import androidx.navigation.NavController
import org.koin.dsl.module
import thuytrinh.hotelsapp.hotelsappnav.R

object HotelListNavigatorImpl : HotelListNavigator {
  override fun navigateToHotelDetails(navController: NavController) {
    navController.navigate(R.id.hotelDetailsFragment)
  }
}

object HotelDetailsNavigatorImpl : HotelDetailsNavigator {
  override fun navigateToBooking(navController: NavController) {
    navController.navigate(R.id.hotelBookingFragment)
  }
}

val navModule = module {
  single<HotelListNavigator> { HotelListNavigatorImpl }
  single<HotelDetailsNavigator> { HotelDetailsNavigatorImpl }
}
