package actions

import com.google.gson.JsonParser
import com.intellij.notification.NotificationDisplayType
import com.intellij.notification.NotificationGroup
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import java.io.ByteArrayInputStream
import java.io.ObjectInputStream

class MyAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val noti = NotificationGroup("myplugin", NotificationDisplayType.BALLOON, true)
        var t = e.getData(LangDataKeys.EDITOR)?.getDocument()?.getText();
        var r = "Test"
        if (t != null) {
            r = t
        }

        //var  text = FileEditorManager.getInstance(e.project!!).getSelectedTextEditor()?.

        noti.createNotification("My Title", r, NotificationType.INFORMATION, null).notify(e.project)

    }
}