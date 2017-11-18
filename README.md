# Weekdays-Selector
Widget for selecting the weekdays

## Screenshots

![Screenshot](/screenshots/Screenshot_Weekdaysselector_1.png)

## Installation

### Gradle

Add the JitPack repository to your root build.gradle file

```groovy
allprojects {
    repositories {
        // ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency

```groovy
dependencies {
    compile 'com.github.DavidProdinger:weekdays-selector:1.0.3'
}
```

### Maven

Add the JitPack repository to your build file

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Add the dependency

```xml
<dependency>
    <groupId>com.github.DavidProdinger</groupId>
    <artifactId>weekdays-selector</artifactId>
    <version>1.0.3</version>
</dependency>
```

## Usage

### In your layout file
```xml
<com.dpro.widgets.WeekdaysPicker
    android:id="@+id/weekdays"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:sunday_first_day="false" />
```

### Your Java file
```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // ...

        WeekdaysPicker widget = (WeekdaysPicker) findViewById(R.id.weekdays);
        widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
            @Override
            public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
                // Do Something
            }
        });
        
        // ...
    }
    
    // ...
}
```

## Configuration

### Layout file

* `enabled`: If user is allowed to select weekdays (default=true)
* `highlight_color`: Color of selected days and unselected Text (default=Color.RED)
* `background_color`: Background-Color of unselected days (default=Color.LTGRAY)
* `text_color`: Color of selected days text (default=Color.WHITE)
* `sunday_first_day`: Starting with Sunday or Monday (default=true)
* `show_weekend`: Display weekend (Satuarday and Sunday) (default=true)

### Java

#### OnChange Listener

See [here](#your-java-file)

## Methods and Functions

Assuming `widget` is an `WeekdaysPicker` object
```java
WeekdaysPicker widget = (WeekdaysPicker) findViewById(R.id.weekdays);
```

### Get selected Days

Returns selected Days as Integer-List according to `Calendar.MONDAY`, `Calendar.TUESDAY` and so on

```java
List<Integer> selectedDays = widget.getSelectedDays();
```

### Get selected Days as Text

Returns selected Days as Text
Optional: Set locale

```java
List<String> selectedDays = widget.getSelectedDaysText();
```

### Select Days

Accept Integer-List of days you would like to select

```java
List<Integer> days = Arrays.asList(Calendar.MONDAY, Calendar.WEDNESDAY, Calendar.FRIDAY, Calendar.SUNDAY);

widget.setSelectedDays(days);
```

### Select one Day

Select day by Integer of Day (eg. `Calendar.SUNDAY`)

```java
widget.selectDay(Calendar.SUNDAY);
```

### Check if all Days are selected

#### One time

```java
if (widget.allDaysSelected()){
    // Do Something
}
```

#### In onchange

```java
widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
    @Override
    public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
        if (selectedDays.size() == 7){
            // Do Something
        }
    }
});
```


### Check if no Day is selected

#### One time

```java
if (widget.noDaySelected()){
    // Do Something
}
```

#### In onchange

```java
widget.setOnWeekdaysChangeListener(new OnWeekdaysChangeListener() {
    @Override
    public void onChange(View view, int clickedDayOfWeek, List<Integer> selectedDays) {
        if (selectedDays.size() == 0){
            // Do Something
        }
    }
});
```

### Check if Selector is editable

```java
if (widget.isInEditMode()){
    // Do Something
}
```

### Set editable

```java
// Enable the widget
widget.setEditable(true);

// Disable the widget
widget.setEditable(false);
```