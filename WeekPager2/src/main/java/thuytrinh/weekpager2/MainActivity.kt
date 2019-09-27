package thuytrinh.weekpager2

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
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

    val weekCount = Int.MAX_VALUE
    val currentWeekPosition = weekCount / 2
    val now = LocalDate.now()
    val weekAdapter = object : RecyclerView.Adapter<WeekViewHolder>() {
      override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeekViewHolder {
        return WeekViewHolder(
          binding = WeekBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
          ).apply {
            viewModel = WeekViewModel(
              getNow = { now },
              getCurrentWeekPosition = { currentWeekPosition },
              getLocale = { Locale.GERMANY },
              onDateClick = { date ->
                binding.toolbar.title = date.asText()
              }
            )
          }
        )
      }

      override fun getItemCount(): Int {
        return weekCount
      }

      override fun onBindViewHolder(holder: WeekViewHolder, position: Int) {
        holder.binding.viewModel?.setWeekPosition(position)
      }
    }
    binding.weekPager.apply {
      adapter = weekAdapter
      setCurrentItem(currentWeekPosition, false)
    }

    binding.toolbar.apply {
      title = now.asText()
      inflateMenu(R.menu.main)
      setOnMenuItemClickListener {
        binding.weekPager.currentItem = currentWeekPosition
        true
      }
    }
  }
}

class WeekViewHolder(val binding: WeekBinding) : RecyclerView.ViewHolder(binding.root)

class WeekViewModel(
  private val getNow: () -> LocalDate,
  private val getCurrentWeekPosition: () -> Int,
  private val getLocale: () -> Locale = { Locale.getDefault() },
  private val onDateClick: (LocalDate) -> Unit = {}
) {
  private var dates = emptyList<LocalDate>()
  private val dateIndices = (0L..6L)
  val daysOfWeek = dateIndices.map { ObservableField<String>() }
  val daysOfMonth = dateIndices.map { ObservableField<String>() }

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
    dates.forEachIndexed { index, date ->
      // e.g. SUNDAY -> SUN
      daysOfWeek[index].set(date.dayOfWeek.name.take(3))
      daysOfMonth[index].set(date.dayOfMonth.toString())
    }
    this.dates = dates
  }

  fun onClick(dateIndex: Int) {
    onDateClick(dates[dateIndex])
  }
}

private val germanDateFormatter = DateTimeFormatter
  .ofPattern("dd.MM.yyyy", Locale.GERMANY)

fun LocalDate.asText(): String {
  return germanDateFormatter.format(this)
}
