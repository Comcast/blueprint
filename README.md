# Blueprint
Blueprint is a library that provides a compact framework for constructing mvp architecture within a multi-view type recycling, scrollable
list UI.  It uses the Android RecyclerView library, and currently only support LinearLayouts

## Design

### Presenter
There are two presentation layers, the ScreenPresenter, and the ComponentViewPresenter.  The ScreenPresenter is repsonsible for creating two things : the list of 
  view types the screen will show (in the correct order), and a map of ComponentViewPresenters, one for each
   view type in the list
   
### View
There are, analogously, two view layers, the ScreenView and the ComponentViews.  The ScreenView's only responsibility is
adding and holding the component view types specified by the ScreenPresenter.  A ScreenViewDelegate is provided as a default
implementation.  The ScreenViewDelegate also contains a convenience function for instantiating the Adapter.  ComponentViews are POJOs
representing the views of the different components.  ComponentViews are 

### ComponentRegistry
Each Component should have a view class that implements ComponentView.  The concrete ComponentViews must be registered with
a ComponentRegistry that maps ComponentView classes with view types.  Optionally, clients can also mark presenters with the
annotation @DefaultPresenter, and pass the annotation a ComponentView class to be associated with.  ScreenPresenters then only
need to specify presenters in the presenterMap if they wish to override the default.

### Adapter
The ComponentAdapter is a RecyclerView Adapter that delegates presentation of each item to the appropriate ComponentViewPresenter, 
based on the view type.  To create one, you need a list of view types in order, and a map of ComponentViewPresenters to view types 
with which to present each view.  in onCreateViewHolder, the Adapter queries the ComponentRegistry and instantiates a concrete
 ComponentView class for the view type, and holds that instance in a List.  Then, in onBindViewHolder, the adapter gets the ComponentView class
 at the bind position, retrieves the ComponentViewPresenter for that ComponentView's view type, and asks the presenter to
 present the data for that bind position into that ComponentView. 

   
### Setup

To use Blueprint, in your project build.gradle add

```xml
compile 'com.xfinity:blueprint-library:<version>'
compile 'com.xfinity:blueprint-annotations:<version>'
```

and either

```xml
annotationProcessor "com.xfinity:blueprint-compiler:<version>"
```

or, if you're using kotlin,
```xml
kapt "com.xfinity:blueprint-compiler:<version>"
```
Current version is 0.9.6
