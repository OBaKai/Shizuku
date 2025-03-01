package moe.shizuku.manager.home

import android.content.Intent
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import moe.shizuku.manager.Helps
import moe.shizuku.manager.R
import moe.shizuku.manager.databinding.HomeItemContainerBinding
import moe.shizuku.manager.databinding.HomeManageAppsItemBinding
import moe.shizuku.manager.ktx.toHtml
import moe.shizuku.manager.management.ApplicationManagementActivity
import moe.shizuku.manager.model.ServiceStatus
import rikka.html.text.HtmlCompat
import rikka.recyclerview.BaseViewHolder
import rikka.recyclerview.BaseViewHolder.Creator

class ManageAppsViewHolder(private val binding: HomeManageAppsItemBinding, root: View) :
    BaseViewHolder<Pair<ServiceStatus, Int>>(root), View.OnClickListener {

    companion object {
        val CREATOR = Creator<Pair<ServiceStatus, Int>> { inflater: LayoutInflater, parent: ViewGroup? ->
            val outer = HomeItemContainerBinding.inflate(inflater, parent, false)
            val inner = HomeManageAppsItemBinding.inflate(inflater, outer.root, true)
            ManageAppsViewHolder(inner, outer.root)
        }
    }

    init {
        root.setOnClickListener(this)
    }

    private inline val title get() = binding.text1
    private inline val summary get() = binding.text2

    override fun onBind() {
        val context = itemView.context
        if (!data.first.isRunning) {
            itemView.isEnabled = false
            title.setText(R.string.home_app_management_title)
            summary.setHtmlText(
                context.getString(
                    R.string.home_status_service_not_running,
                    context.getString(R.string.app_name)
                )
            )
        } else if (data.first.permission) {
            itemView.isEnabled = true
            title.setHtmlText(
                context.resources.getQuantityString(
                    R.plurals.home_app_management_authorized_apps_count,
                    data.second,
                    data.second
                )
            )
            summary.setHtmlText(context.getString(R.string.home_app_management_view_authorized_apps))
        } else {
            itemView.isEnabled = false
            title.setText(R.string.home_app_management_title)

            val sb = StringBuilder()
            sb.append(context.getString(R.string.app_management_dialog_adb_is_limited_title))
            sb.append("<br>")
            sb.append(context.getString(R.string.app_management_dialog_adb_is_limited_message, Helps.ADB.get()))

            summary.movementMethod = LinkMovementMethod.getInstance()
            summary.text = sb.toHtml(HtmlCompat.FROM_HTML_OPTION_TRIM_WHITESPACE)
        }
    }

    override fun onClick(v: View) {
        v.context.startActivity(Intent(v.context, ApplicationManagementActivity::class.java))
    }
}
