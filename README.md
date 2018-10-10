[![Maven Central][mavenbadge-svg]][mavencentral]

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

[mavenbadge-svg]: https://maven-badges.herokuapp.com/maven-central/com.xfinity/blueprint-library/badge.svg
[mavencentral]: https://search.maven.org/artifact/com.xfinity/blueprint-library

### Usage

#### Creating Component
After creating your layout, create a view holder class and annotate with @ComponentViewHolder(viewType = R.layout.layout_name) 

![alt text](https://github.com/Comcast/blueprint/blob/mrtvrgn-doc-v2/layout_viewHolder.PNG)

When you create a view class and annotate with @ComponentViewClass(viewHolderClass = yourViewHolderClass), you can build the project. Base view class will be available for you to extend from.

![alt text](https://github.com/Comcast/blueprint/blob/mrtvrgn-doc-v2/viewClass.PNG)

Finally, if you have a model that you wish to use in the component, you can simple extend from ComponentModel and create you component presenter and to create you need to extend from ComponentPresenter by passing view and model as Type parameters.

![alt text](https://github.com/Comcast/blueprint/blob/mrtvrgn-doc-v2/componentModel.PNG)

At this point, the your component presenter should give you the view and model with present(view, model) method.

To display this component, simply add to your screenView in any of your screen presenter.

