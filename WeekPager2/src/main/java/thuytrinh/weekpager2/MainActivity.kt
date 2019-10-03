package thuytrinh.weekpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.map
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.temporal.WeekFields
import thuytrinh.weekpager2.databinding.ActivityMainBinding
import thuytrinh.weekpager2.databinding.WeekBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.week)
    val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
      this,
      R.layout.activity_main
    )
    val viewModel = WeekPagerViewModel()
    binding.viewModel = viewModel
    binding.lifecycleOwner = this

    val weekPagerAdapter = WeekPagerAdapter(viewModel)
    binding.weekPager.apply {
      adapter = weekPagerAdapter
      setCurrentItem(viewModel.currentWeekPosition, false)
    }
    viewModel.selectedDate.observe(this, Observer {
      weekPagerAdapter.notifyItemChanged(binding.weekPager.currentItem - 1)
      weekPagerAdapter.notifyItemChanged(binding.weekPager.currentItem + 1)
    })

    binding.toolbar.apply {
      inflateMenu(R.menu.main)
      setOnMenuItemClickListener {
        binding.weekPager.currentItem = viewModel.currentWeekPosition
        true
      }
    }
  }
}

class WeekPagerAdapter(
  private val weekPagerViewModel: WeekPagerViewModel
) : RecyclerView.Adapter<WeekViewHolder>() {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
    return WeekViewHolder(
      weekBinding = WeekBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false
      ),
      viewModel = WeekViewModel(
        getNow = { weekPagerViewModel.now },
        getCurrentWeekPosition = { weekPagerViewModel.currentWeekPosition },
        getLocale = { Locale.GERMANY },
        onDateClick = { weekPagerViewModel.selectedDate.value = it },
        getSelectedDate = { weekPagerViewModel.selectedDate.value }
      )
    )
  }

  override fun getItemCount(): Int {
    return weekPagerViewModel.weekCount
  }

  override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
    holder.setWeekPosition(position)
  }
}

class WeekViewHolder(
  weekBinding: WeekBinding,
  private val viewModel: WeekViewModel
) : RecyclerView.ViewHolder(weekBinding.root) {
  init {
    weekBinding.viewModel = viewModel
    listOf(
      weekBinding.date0,
      weekBinding.date1,
      weekBinding.date2,
      weekBinding.date3,
      weekBinding.date4,
      weekBinding.date5,
      weekBinding.date6
    ).forEachIndexed { dateIndex, dateBinding ->
      dateBinding.root.setOnClickListener { viewModel.onDateClick(dateIndex) }
    }
  }

  fun setWeekPosition(weekPosition: Int) {
    viewModel.setWeekPosition(weekPosition)
  }
}

class WeekPagerViewModel {
  val weekCount = Int.MAX_VALUE
  val now: LocalDate = LocalDate.now()
  val currentWeekPosition = weekCount / 2

  val selectedDate = MutableLiveData<LocalDate>(now)
  val selectedDateText = selectedDate.map { it.asText() }

  private val germanDateFormatter = DateTimeFormatter
    .ofPattern("dd.MM.yyyy", Locale.GERMANY)

  private fun LocalDate.asText(): String {
    return germanDateFormatter.format(this)
  }
}

data class IsSelected(
  val value: Boolean,
  val hasAnimation: Boolean = false
)

data class DateViewModel(
  val dayOfWeek: String,
  val dayOfMonth: String,
  val isSelected: IsSelected
)

class WeekViewModel(
  private val getNow: () -> LocalDate,
  private val getCurrentWeekPosition: () -> Int,
  private val getLocale: () -> Locale = { Locale.getDefault() },
  private val onDateClick: (LocalDate) -> Unit,
  private val getSelectedDate: () -> LocalDate?
) {
  private var dates = emptyList<LocalDate>()
  private val dateIndices = (0L..6L)
  val dateViewModels = dateIndices.map { ObservableField<DateViewModel>() }

  fun setWeekPosition(weekPosition: Int) {
    val dates = getAllDatesForThisWeek(weekPosition)
    dateViewModels.forEachIndexed { i, field ->
      field.set(
        DateViewModel(
          // e.g. SUNDAY -> SUN
          dayOfWeek = dates[i].dayOfWeek.name.take(3),
          dayOfMonth = dates[i].dayOfMonth.toString(),
          isSelected = when {
            dates[i].isEqual(getSelectedDate()) -> IsSelected(value = true)
            else -> IsSelected(value = false)
          }
        )
      )
    }

    this.dates = dates
  }

  private fun getAllDatesForThisWeek(weekPosition: Int): List<LocalDate> {
    val now: LocalDate = getNow()
    val weekCountDiff = weekPosition.toLong() - getCurrentWeekPosition()
    val firstDateOfWeek = now
      .plusWeeks(weekCountDiff)
      .run {
        with(WeekFields.of(getLocale()).firstDayOfWeek)
      }
    return dateIndices.map { firstDateOfWeek.plusDays(it) }
  }

  fun onDateClick(dateIndex: Int) {
    val selectedDate = dates[dateIndex]
    onDateClick(selectedDate)
    showSelection(selectedDate)
  }

  private fun showSelection(selectedDate: LocalDate) {
    dateViewModels.forEachIndexed { i, field ->
      field.set(
        field.get()?.run {
          val newIsSelected = when {
            dates[i].isEqual(selectedDate) -> IsSelected(value = true, hasAnimation = true)
            else -> if (isSelected.value) {
              IsSelected(value = false, hasAnimation = true)
            } else {
              IsSelected(value = false)
            }
          }
          if (newIsSelected.value != isSelected.value) {
            copy(isSelected = newIsSelected)
          } else {
            this
          }
        }
      )
    }
  }
}
