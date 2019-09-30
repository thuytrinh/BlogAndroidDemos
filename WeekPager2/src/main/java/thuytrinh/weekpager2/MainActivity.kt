package thuytrinh.weekpager2

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
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
    binding.weekPager.apply {
      adapter = WeekPagerAdapter(viewModel)
      setCurrentItem(viewModel.currentWeekPosition, false)
    }
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
  private var selectedWeekViewModel: WeekViewModel? = null

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
        onDateClick = { date ->
          weekPagerViewModel.selectedDate.value = date
          selectedWeekViewModel?.refreshSelection()
        },
        getSelectedDate = { weekPagerViewModel.selectedDate.value },
        onSelected = { selectedWeekViewModel = it }
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

@BindingAdapter("isSelected")
fun ImageView.setIsSelected(isSelected: IsSelected) {
  setBackgroundResource(R.drawable.circle)
  if (isSelected.value) {
    if (isSelected.hasAnimation) {
      animate().alpha(1f)
    } else {
      alpha = 1f
    }
  } else {
    if (isSelected.hasAnimation) {
      animate().alpha(0f)
    } else {
      alpha = 0f
    }
  }
}

@BindingAdapter("isSelected")
fun TextView.setIsSelected(isSelected: IsSelected) {
  if (isSelected.value) {
    if (isSelected.hasAnimation) {
      ValueAnimator.ofArgb(
        ContextCompat.getColor(context, android.R.color.black),
        ContextCompat.getColor(context, android.R.color.white)
      ).apply {
        addUpdateListener {
          setTextColor(it.animatedValue as Int)
        }
        start()
      }
    } else {
      setTextColor(ContextCompat.getColor(context, android.R.color.white))
    }
  } else {
    if (isSelected.hasAnimation) {
      ValueAnimator.ofArgb(
        ContextCompat.getColor(context, android.R.color.white),
        ContextCompat.getColor(context, android.R.color.black)
      ).apply {
        addUpdateListener {
          setTextColor(it.animatedValue as Int)
        }
        start()
      }
    } else {
      setTextColor(ContextCompat.getColor(context, android.R.color.black))
    }
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
  private val getSelectedDate: () -> LocalDate?,
  private val onSelected: (WeekViewModel) -> Unit
) {
  private var dates = emptyList<LocalDate>()
  private val dateIndices = (0L..6L)
  val dateViewModels = dateIndices.map { ObservableField<DateViewModel>() }

  fun setWeekPosition(weekPosition: Int) {
    val now = getNow()
    val currentWeekPosition = getCurrentWeekPosition()
    val firstDayOfWeek = now
      .plusWeeks(weekPosition.toLong() - currentWeekPosition)
      .run {
        val firstDayOfWeek = WeekFields.of(getLocale()).firstDayOfWeek
        with(firstDayOfWeek)
      }
    val dates = dateIndices.map { firstDayOfWeek.plusDays(it) }
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

    if (dates.any { it.isEqual(getSelectedDate()) }) {
      onSelected(this)
    }
  }

  fun onDateClick(dateIndex: Int) {
    onDateClick(dates[dateIndex])
    onSelected(this)
  }

  fun refreshSelection() {
    dateViewModels.forEachIndexed { i, field ->
      field.set(
        field.get()?.run {
          val newIsSelected = when {
            dates[i].isEqual(getSelectedDate()) -> IsSelected(value = true, hasAnimation = true)
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
