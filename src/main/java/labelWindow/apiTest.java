package labelWindow;


import com.android.ide.common.rendering.api.RenderSession;
import com.android.ide.common.rendering.api.Result;
import com.android.ide.common.resources.ResourceItem;
import com.android.ide.common.resources.ResourceRepository;
import com.android.ide.common.resources.ResourceResolver;
import com.android.ide.common.resources.configuration.FolderConfiguration;
import com.android.io.FolderWrapper;
import com.android.resources.*;
import org.xmlpull.v1.XmlPullParserException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class apiTest {

    // path to the SDK and the project to render
    //private final static String SDK = "/c/Users/ansross/AppData/Local/Android/Sdk";
    private final static String PROJECT = "/c/Users/ansross/Documents/Research/PlugIn/Repositories/MyApplication";
    private final static String SDK = "C:\\Users\\ansross\\AppData\\Local\\Android\\Sdk";
    /**
     * @param args
     */
    public static void main(String[] args) {
        // load the factory for a given platform
        File f = new File(SDK + "/platforms/android-29");
        RenderServiceFactory factory = RenderServiceFactory.create(f);

        if (factory == null) {
            System.err.println("Failed to load platform rendering library");
            System.exit(1);
        }


        FolderWrapper fw = new FolderWrapper(PROJECT + "/res");
        // load the project resources
        ResourceRepository projectRes = new ResourceRepository(fw,false /*isFramework*/) {

            @Override
            protected ResourceItem createResourceItem(String name) {
                return new ResourceItem(name);
            }
        };
        //try {
            projectRes.loadResources();
        /*} catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }*/

        // create the rendering config
        FolderConfiguration config = RenderServiceFactory.createConfig(
                1280, 800, // size 1 and 2. order doesn't matter.
                // Orientation will drive which is w and h
                ScreenSize.XLARGE,
                ScreenRatio.LONG,
                ScreenOrientation.LANDSCAPE,
                Density.MEDIUM,
                TouchScreen.FINGER,
                KeyboardState.SOFT,
                Keyboard.QWERTY,
                NavigationState.EXPOSED,
                Navigation.NONAV,
                12); // api level

        // create the resource resolver once for the given config.
        ResourceResolver resources = factory.createResourceResolver(
                config, projectRes,
                "Theme", false /*isProjectTheme*/);

        // create the render service
        RenderService renderService = factory.createService(
                resources, config, new ProjectCallback());

        try {
            RenderSession session = renderService
                    .setLog(new StdOutLogger())
                    .setAppInfo("foo", "icon") // optional
                    .createRenderSession("main" /*layoutName*/);

            // get the status of the render
            Result result = session.getResult();
            if (result.isSuccess() == false) {
                System.err.println(result.getErrorMessage());
                System.exit(1);
            }

            // get the image and save it somewhere.
            BufferedImage image = session.getImage();
            ImageIO.write(image, "png", new File("/path/to/test.png"));

            // read the views
            //displayViewObjects(session.getRootViews());

            return;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.exit(1);
    }
}
