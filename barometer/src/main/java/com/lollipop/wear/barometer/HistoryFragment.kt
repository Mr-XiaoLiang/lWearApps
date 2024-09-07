package com.lollipop.wear.barometer

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.viewbinding.ViewBinding
import com.lollipop.wear.barometer.databinding.FragmentHistoryBinding
import com.lollipop.wear.barometer.databinding.ItemHistoryBinding
import com.lollipop.wear.barometer.databinding.ItemSpaceBinding
import com.lollipop.wear.basic.BasicFragment
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryFragment : BasicFragment() {

    private val binding by lazy {
        FragmentHistoryBinding.inflate(layoutInflater)
    }

    private val dataList = mutableListOf<HistoryInfo>()
    private val adapter by lazy {
        PressureAdapter(dataList)
    }
    private val loadMoreHelper by lazy {
        LoadMoreHelper(onLoadMore = ::onLoadMore)
    }
    private var callback: Callback? = null

    private var modeCount = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = findCallback(context)
    }

    override fun onDetach() {
        super.onDetach()
        callback = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(
            view.context, RecyclerView.VERTICAL, false
        )
        loadMoreHelper.attach(binding.recyclerView)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onResume() {
        super.onResume()
        dataList.clear()
        dataList.add(HistoryInfo.Space)
        adapter.notifyDataSetChanged()
        onLoadMore()
    }

    private fun onLoadMore() {
        modeCount++
        val localMode = modeCount
        val maxTime = findLastTime()
        callback?.loadMore(maxTime) { result ->
            if (!isResumed) {
                return@loadMore
            }
            if (localMode != modeCount) {
                return@loadMore
            }
            if (result.isEmpty()) {
                return@loadMore
            }
            val start = dataList.size
            dataList.addAll(result.map { HistoryInfo.Pressure(it) })
            adapter.notifyItemRangeInserted(start, result.size)
            loadMoreHelper.isLoading = false
        }
    }

    private fun findLastTime(): Long {
        if (dataList.isEmpty()) {
            System.currentTimeMillis()
        }
        for (index in dataList.lastIndex downTo 0) {
            val item = dataList[index]
            if (item is HistoryInfo.Pressure) {
                return item.info.time
            }
        }
        return System.currentTimeMillis()
    }

    fun onNewPressure(info: PressureInfo) {
        if (!isResumed) {
            return
        }
        if (dataList.isEmpty()) {
            dataList.add(HistoryInfo.Space)
            dataList.add(HistoryInfo.Pressure(info))
            adapter.notifyItemRangeInserted(0, 2)
        } else {
            var isInsert = false
            for (index in dataList.indices) {
                val item = dataList[index]
                // 插入第一个不是Space的地方
                if (item is HistoryInfo.Pressure) {
                    dataList.add(index, HistoryInfo.Pressure(info))
                    adapter.notifyItemInserted(index)
                    isInsert = true
                    break
                }
            }
            if (!isInsert) {
                dataList.add(HistoryInfo.Pressure(info))
                adapter.notifyItemInserted(dataList.size - 1)
            }
        }
    }

    private sealed class HistoryInfo {
        data object Space : HistoryInfo()
        class Pressure(val info: PressureInfo) : HistoryInfo()
    }

    private class PressureAdapter(val data: List<HistoryInfo>) :
        RecyclerView.Adapter<HistoryItemHolder>() {

        companion object {
            private const val TYPE_SPACE = 0
            private const val TYPE_PRESSURE = 1
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryItemHolder {
            when (viewType) {
                TYPE_PRESSURE -> {
                    return HistoryItemHolder.Pressure(
                        ItemHistoryBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }

                else -> {
                    return HistoryItemHolder.Space(
                        ItemSpaceBinding.inflate(
                            LayoutInflater.from(parent.context),
                            parent,
                            false
                        )
                    )
                }
            }
        }

        override fun getItemCount(): Int {
            return data.size
        }

        override fun getItemViewType(position: Int): Int {
            return when (data[position]) {
                is HistoryInfo.Pressure -> TYPE_PRESSURE
                HistoryInfo.Space -> TYPE_SPACE
            }
        }

        override fun onBindViewHolder(holder: HistoryItemHolder, position: Int) {
            if (holder is HistoryItemHolder.Pressure) {
                val info = data[position]
                if (info is HistoryInfo.Pressure) {
                    holder.bind(info.info)
                }
            }
        }

    }

    private sealed class HistoryItemHolder(
        binding: ViewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        class Space(binding: ItemSpaceBinding) : HistoryItemHolder(binding)

        class Pressure(
            private val binding: ItemHistoryBinding
        ) : HistoryItemHolder(binding) {

            private val pressureFormat = DecimalFormat("0.00hPa")
            private val altitudeFormat = DecimalFormat("0.00m")
            private val timeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            private val pressureDrawable = ProgressBarDrawable()
            private val altitudeDrawable = ProgressBarDrawable()

            init {
                binding.pressureValueView.background = pressureDrawable
                binding.altitudeValueView.background = altitudeDrawable
                pressureDrawable.color = ContextCompat.getColor(
                    itemView.context, R.color.bg_pressure_bar
                )
                altitudeDrawable.color = ContextCompat.getColor(
                    itemView.context, R.color.bg_altitude_bar
                )
            }

            fun bind(info: PressureInfo) {
                binding.sensorStateView.setBackgroundResource(
                    BarometerHelper.getSensorStateColor(
                        info.sensorState
                    )
                )
                binding.pressureValueView.text = pressureFormat.format(info.pressure.toDouble())
                binding.altitudeValueView.text = altitudeFormat.format(info.altitude.toDouble())
                binding.timeView.text = timeFormat.format(Date(info.time))
                pressureDrawable.progress = BarometerHelper.getPressureProgress(info.pressure)
                altitudeDrawable.progress = BarometerHelper.getAltitudeProgress(info.altitude)
            }

        }

    }

    private class LoadMoreHelper(
        private val keepSize: Int = 5,
        private val onLoadMore: () -> Unit
    ) : RecyclerView.OnScrollListener() {

        var isLoading = false

        fun attach(recyclerView: RecyclerView) {
            recyclerView.addOnScrollListener(this)
        }

        fun check(recyclerView: RecyclerView) {
            if (isLoading) {
                return
            }
            val layoutManager = recyclerView.layoutManager ?: return
            val adapter = recyclerView.adapter ?: return
            val itemCount = adapter.itemCount
            val maxPosition = itemCount - 1
            if (itemCount < keepSize) {
                notifyLoadMore()
                return
            }
            when (layoutManager) {
                is LinearLayoutManager -> {
                    val lastPosition = layoutManager.findLastVisibleItemPosition()
                    if ((maxPosition - lastPosition) <= keepSize) {
                        notifyLoadMore()
                        return
                    }
                }

                is StaggeredGridLayoutManager -> {
                    val lastPositions = layoutManager.findLastVisibleItemPositions(null)
                    if (lastPositions.any { (maxPosition - it) <= keepSize }) {
                        notifyLoadMore()
                        return
                    }
                }
            }
        }

        private fun notifyLoadMore() {
            isLoading = true
            onLoadMore()
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            check(recyclerView)
        }

    }

    private class ProgressBarDrawable : Drawable() {

        private val paint = Paint().apply {
            style = Paint.Style.FILL
            isDither = true
            isAntiAlias = true
        }

        var color: Int
            get() {
                return paint.color
            }
            set(value) {
                paint.color = value
            }

        private val barBounds = RectF()

        var progress: Float = 0F
            set(value) {
                field = value
                updateBarBounds()
            }

        override fun onBoundsChange(bounds: Rect) {
            super.onBoundsChange(bounds)
            updateBarBounds()
        }

        private fun updateBarBounds() {
            barBounds.set(
                bounds.left.toFloat() + ((1 - progress) * bounds.width()),
                bounds.top.toFloat(),
                bounds.right.toFloat(),
                bounds.bottom.toFloat()
            )
            invalidateSelf()
        }

        override fun draw(canvas: Canvas) {
            canvas.drawRect(barBounds, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun getOpacity(): Int {
            return PixelFormat.TRANSPARENT
        }


    }

    interface Callback {
        fun loadMore(lastTime: Long, callback: (List<PressureInfo>) -> Unit)
    }

}