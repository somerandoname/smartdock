package cu.axel.smartdock.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.RecyclerView
import cu.axel.smartdock.R
import cu.axel.smartdock.models.DockApp
import cu.axel.smartdock.utils.AppUtils
import cu.axel.smartdock.utils.ColorUtils
import cu.axel.smartdock.utils.IconPackUtils
import cu.axel.smartdock.utils.Utils

class DockAppAdapter(
    private val context: Context, private var apps: ArrayList<DockApp>,
    private val listener: OnDockAppClickListener, private val iconPackUtils: IconPackUtils?
) : RecyclerView.Adapter<DockAppAdapter.ViewHolder>() {
    private var iconBackground = 0
    private val iconPadding: Int
    private val iconTheming: Boolean
    private val tintIndicators: Boolean

    // Icon box size and the gap between two icons, in pixels. DockService derives
    // both from the dock height and sets them before the icons are bound.
    var iconSizePx: Int = 0
    var iconGapPx: Int = 0

    interface OnDockAppClickListener {
        fun onDockAppClicked(app: DockApp, view: View)
        fun onDockAppLongClicked(app: DockApp, view: View)
    }

    init {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        iconTheming = sharedPreferences.getString("icon_pack", "") != ""
        iconPadding =
            Utils.dpToPx(context, sharedPreferences.getString("icon_padding", "5")!!.toInt())
        tintIndicators = sharedPreferences.getBoolean("tint_indicators", false)
        when (sharedPreferences.getString("icon_shape", "circle")) {
            "circle" -> iconBackground = R.drawable.circle
            "round_rect" -> iconBackground = R.drawable.round_square
            "default" -> iconBackground = -1
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, arg1: Int): ViewHolder {
        val itemLayoutView =
            LayoutInflater.from(parent.context).inflate(R.layout.app_task_entry, parent, false)
        return ViewHolder(itemLayoutView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val app = apps[position]
        val size = app.tasks.size
        // Set icon size (width and height)
        val iconLayoutParams = viewHolder.iconIv.layoutParams
        iconLayoutParams.width = iconSizePx
        iconLayoutParams.height = iconSizePx
        viewHolder.iconIv.layoutParams = iconLayoutParams
        // Half the gap on each side of the icon
        val parent = viewHolder.iconIv.parent
        if (parent is View) {
            val params = parent.layoutParams
            if (params is ViewGroup.MarginLayoutParams) {
                params.marginStart = iconGapPx / 2
                params.marginEnd = iconGapPx / 2
                parent.layoutParams = params
            }
        }
        // Do NOT set icon padding based on spacing, only use iconPadding if needed for theming
        // (handled below in the iconBackground block)

        if (size > 0) {
            if (tintIndicators) ColorUtils.applyColor(
                viewHolder.runningIndicator,
                ColorUtils.manipulateColor(ColorUtils.getDrawableDominantColor(app.icon), 2f)
            )
            if (app.tasks[0].id != -1) viewHolder.runningIndicator.alpha = 1f
            if (app.packageName == AppUtils.currentApp) {
                viewHolder.runningIndicator.layoutParams.width = Utils.dpToPx(context, 16)
            }
            if (size > 1) {
                viewHolder.taskCounter.text = "" + size
                viewHolder.taskCounter.alpha = 1f
            }
        }
        if (iconPackUtils != null)
            viewHolder.iconIv.setImageDrawable(iconPackUtils.getAppThemedIcon(app.packageName))
        else
            viewHolder.iconIv.setImageDrawable(app.icon)
        if (iconBackground != -1) {
            viewHolder.iconIv.setPadding(iconPadding, iconPadding, iconPadding, iconPadding)
            viewHolder.iconIv.setBackgroundResource(iconBackground)
            ColorUtils.applyColor(
                viewHolder.iconIv,
                ColorUtils.getDrawableDominantColor(viewHolder.iconIv.drawable)
            )
        }
        viewHolder.bind(app, listener)
    }

    override fun getItemCount(): Int {
        return apps.size
    }

    fun updateApps(newApps: List<DockApp>) {
        this.apps.clear()
        this.apps.addAll(newApps)
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iconIv: ImageView
        var taskCounter: TextView
        var runningIndicator: View

        init {
            iconIv = itemView.findViewById(R.id.icon_iv)
            runningIndicator = itemView.findViewById(R.id.running_indicator)
            taskCounter = itemView.findViewById(R.id.task_count_badge)
        }

        fun bind(app: DockApp, listener: OnDockAppClickListener) {
            itemView.setOnClickListener { view -> listener.onDockAppClicked(app, view) }
            itemView.setOnLongClickListener { view ->
                listener.onDockAppLongClicked(app, view)
                true
            }
            itemView.setOnTouchListener { view, event ->
                if (event.buttonState == MotionEvent.BUTTON_SECONDARY) {
                    listener.onDockAppLongClicked(app, view)
                    return@setOnTouchListener true
                }
                false
            }
        }
    }
}