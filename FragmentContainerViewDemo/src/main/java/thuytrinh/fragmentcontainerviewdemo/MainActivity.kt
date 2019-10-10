package thuytrinh.fragmentcontainerviewdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import thuytrinh.fragmentcontainerviewdemo.databinding.MainBinding

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    DataBindingUtil.setContentView<MainBinding>(this, R.layout.main)
  }
}
