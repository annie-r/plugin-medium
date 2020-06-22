package labelWindow;

import com.android.ide.common.rendering.LayoutLibrary;
import com.android.ide.common.rendering.api.AttrResourceValue;
import com.android.ide.common.rendering.api.DeclareStyleableResourceValue;
import com.android.ide.common.rendering.api.IProjectCallback;
import com.android.ide.common.rendering.api.ResourceValue;
import com.android.ide.common.resources.FrameworkResources;
import com.android.ide.common.resources.ResourceRepository;
import com.android.ide.common.resources.ResourceResolver;
import com.android.ide.common.resources.configuration.*;
import com.android.ide.common.sdk.LoadStatus;
import com.android.io.FileWrapper;
import com.android.io.FolderWrapper;
import com.android.resources.*;
import com.android.sdklib.SdkConstants;
import com.android.sdklib.internal.project.ProjectProperties;
import com.android.utils.ILogger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RenderServiceFactory {
    private LayoutLibrary mLibrary;
    private FrameworkResources mResources;

    public static RenderServiceFactory create(File platformFolder) {

        // create the factory
        RenderServiceFactory factory = new RenderServiceFactory();

        if (factory.loadLibrary(platformFolder)) {
            return factory;
        }

        return null;
    }

    /**
     * Creates a config. This must be a valid config like a device would return. This is to
     * prevent issues where some resources don't exist in all cases and not in the default
     * (for instance only available in hdpi and mdpi but not in default).
     *
     * @param size1
     * @param size2
     * @param screenSize
     * @param screenRatio
     * @param orientation
     * @param density
     * @param touchScreen
     * @param keyboardState
     * @param keyboard
     * @param navigationState
     * @param navigation
     * @param apiLevel
     * @return
     */
    public static FolderConfiguration createConfig(
            int size1,
            int size2,
            ScreenSize screenSize,
            ScreenRatio screenRatio,
            ScreenOrientation orientation,
            Density density,
            TouchScreen touchScreen,
            KeyboardState keyboardState,
            Keyboard keyboard,
            NavigationState navigationState,
            Navigation navigation,
            int apiLevel) {
        FolderConfiguration config = new FolderConfiguration();

        int width = size1, height = size2;
        switch (orientation) {
            case LANDSCAPE:
                width = size1 < size2 ? size2 : size1;
                height = size1 < size2 ? size1 : size2;
                break;
            case PORTRAIT:
                width = size1 < size2 ? size1 : size2;
                height = size1 < size2 ? size2 : size1;
                break;
            case SQUARE:
                width = height = size1;
                break;
        }

        int wdp = (width * Density.DEFAULT_DENSITY) / density.getDpiValue();
        int hdp = (height * Density.DEFAULT_DENSITY) / density.getDpiValue();

        config.addQualifier(new SmallestScreenWidthQualifier(wdp < hdp ? wdp : hdp));
        config.addQualifier(new ScreenWidthQualifier(wdp));
        config.addQualifier(new ScreenHeightQualifier(hdp));

        config.addQualifier(new ScreenSizeQualifier(screenSize));
        config.addQualifier(new ScreenRatioQualifier(screenRatio));
        config.addQualifier(new ScreenOrientationQualifier(orientation));
        config.addQualifier(new DensityQualifier(density));
        config.addQualifier(new TouchScreenQualifier(touchScreen));
        config.addQualifier(new KeyboardStateQualifier(keyboardState));
        config.addQualifier(new TextInputMethodQualifier(keyboard));
        config.addQualifier(new NavigationStateQualifier(navigationState));
        config.addQualifier(new NavigationMethodQualifier(navigation));
        config.addQualifier(width > height ? new ScreenDimensionQualifier(width, height) :
                new ScreenDimensionQualifier(height, width));
        config.addQualifier(new VersionQualifier(apiLevel));

        config.updateScreenWidthAndHeight();

        return config;
    }

    /**
     * Returns a {@link ResourceResolver} for a given config and project resource.
     *
     * @param config
     * @param projectResources
     * @param themeName
     * @param isProjectTheme
     * @return
     */
    public ResourceResolver createResourceResolver(
            FolderConfiguration config,
            ResourceRepository projectResources,
            String themeName,
            boolean isProjectTheme) {

        Map<ResourceType, Map<String, ResourceValue>> configedProjectRes =
                projectResources.getConfiguredResources(config);

        Map<ResourceType, Map<String, ResourceValue>> configedFrameworkRes =
                mResources.getConfiguredResources(config);

        return ResourceResolver.create(configedProjectRes, configedFrameworkRes,
                themeName, isProjectTheme);
    }

    /**
     * Creates a RenderService
     *
     * @param resources
     * @param config
     * @param projectCallback
     * @return
     */
    public RenderService createService(
            ResourceResolver resources,
            FolderConfiguration config,
            IProjectCallback projectCallback) {
        RenderService renderService = new RenderService(
                mLibrary, resources, config, projectCallback);

        return renderService;

    }

    /**
     * Creates a RenderService. This is less efficient than
     * {@link #createService(ResourceResolver, FolderConfiguration, IProjectCallback)} since the
     * {@link ResourceResolver} object is not cached by the caller.
     *
     * @param projectResources
     * @param themeName
     * @param isProjectTheme
     * @param config
     * @param projectCallback
     * @return
     */
    public RenderService createService(
            ResourceRepository projectResources,
            String themeName,
            boolean isProjectTheme,
            FolderConfiguration config,
            IProjectCallback projectCallback) {
        ResourceResolver resources = createResourceResolver(
                config, projectResources, themeName, isProjectTheme);

        RenderService renderService = new RenderService(
                mLibrary, resources, config, projectCallback);

        return renderService;
    }

    private RenderServiceFactory() {

    }

    private boolean loadLibrary(File platformFolder) {
        if (platformFolder.isDirectory() == false) {
            throw new IllegalArgumentException("platform folder does not exist.");
        }

        File dataFolder = new File(platformFolder, "data");
        if (dataFolder.isDirectory() == false) {
            throw new IllegalArgumentException("platform data folder does not exist.");
        }

        File layoutLibJar = new File(dataFolder, "layoutlib.jar");
        if (layoutLibJar.isFile() == false) {
            throw new IllegalArgumentException("platform layoutlib.jar does not exist.");
        }

        File resFolder = new File(dataFolder, "res");
        if (resFolder.isDirectory() == false) {
            throw new IllegalArgumentException("platform res folder does not exist.");
        }

        File fontFolder = new File(dataFolder, "fonts");
        if (fontFolder.isDirectory() == false) {
            throw new IllegalArgumentException("platform font folder does not exist.");
        }

        FileWrapper buildProp = new FileWrapper(platformFolder, SdkConstants.FN_BUILD_PROP);
        if (buildProp.isFile() == false) {
            throw new IllegalArgumentException("platform build.prop does not exist.");
        }

        StdOutLogger log = new StdOutLogger();



        mLibrary = LayoutLibrary.load(layoutLibJar.getAbsolutePath(), log,
                "LayoutLibRenderer");
        if (mLibrary.getStatus() != LoadStatus.LOADED) {
            throw new IllegalArgumentException(mLibrary.getLoadMessage());
        }


        // load the framework resources
        mResources = loadResources(resFolder, log);

        // get all the attr values.
        HashMap<String, Map<String, Integer>> enumMap = new HashMap<String, Map<String, Integer>>();

        FolderConfiguration config = new FolderConfiguration();
        Map<ResourceType, Map<String, ResourceValue>> res =
                mResources.getConfiguredResources(config);

        // get the ATTR values
        Map<String, ResourceValue> attrItems = res.get(ResourceType.ATTR);
        for (ResourceValue value : attrItems.values()) {
            if (value instanceof AttrResourceValue) {
                AttrResourceValue attr = (AttrResourceValue) value;
                Map<String, Integer> values = attr.getAttributeValues();
                if (values != null) {
                    enumMap.put(attr.getName(), values);
                }
            }
        }

        // get the declare-styleable values
        Map<String, ResourceValue> styleableItems = res.get(ResourceType.STYLEABLE);// DECLARE_STYLEABLE);

        // get the attr from the styleable
        for (ResourceValue value : styleableItems.values()) {
            if (value instanceof DeclareStyleableResourceValue) {
                DeclareStyleableResourceValue dsrc = (DeclareStyleableResourceValue) value;
                Map<String, AttrResourceValue> attrs = dsrc.getAllAttributes();
                if (attrs != null && attrs.size() > 0) {
                    for (AttrResourceValue attr : attrs.values()) {
                        Map<String, Integer> values = attr.getAttributeValues();
                        if (values != null) {
                            enumMap.put(attr.getName(), values);
                        }
                    }
                }
            }
        }

        // we need to parse the build.prop for this
        Map<String, String> buildPropMap = ProjectProperties.parsePropertyFile(buildProp, log);

        return mLibrary.init(buildPropMap, fontFolder, enumMap, log);
    }
/*
    private FrameworkResources loadResources(File resFolder, ILogger log) {
        FrameworkResources resources = new FrameworkResources();

        try {
            FolderWrapper path = new FolderWrapper(resFolder);
            resources.loadResources(path);
            resources.loadPublicResources(path, log);
            return resources;
        } catch (IOException e) {
            // since we test that folders are folders, and files are files, this shouldn't
            // happen. We can ignore it.
        }

        return null;
    }
*/
    private FrameworkResources loadResources(File resFolder, ILogger log) {




        //FrameworkResources resources = new FrameworkResources();

            FolderWrapper path = new FolderWrapper(resFolder);
            FrameworkResources resources = new FrameworkResources(path);
            resources.loadResources();
            resources.loadPublicResources(log);
            return resources;




    }

}
