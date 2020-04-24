package labelWindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * Created by IntelliJ IDEA.
 * User: Alexey.Chursin
 * Date: Aug 25, 2010
 * Time: 2:09:00 PM
 */
public class LabelWindowFactory implements ToolWindowFactory {
  // Create the tool window content.
  public void createToolWindowContent(Project project, ToolWindow toolWindow) {
    /*PsiFile f;
    FilenameIndex.getFilesByName(project, "content_main.xml", GlobalSearchScope.projectScope(project))[0]
            .accept(new PsiRecursiveElementWalkingVisitor(){
              @Override
              public void visitElement(PsiElement element) {
                PsiElement e = element;
                super.visitElement(element);
              }
            });*/
    LabelWindow labelWindow = new LabelWindow(toolWindow, project);
    ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
    Content content = contentFactory.createContent(labelWindow.getContent(), "", false);
    toolWindow.getContentManager().addContent(content);
  }
}