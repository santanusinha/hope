# 2.0.5
- A host of Date functions are now supported as built-in functions 
  - `date.now()`, `date.secondOfMinute()`, `date.minuteOfHour()`, `date.hourOfDay()`, `date.dayOfWeek()`, `date.dayOfMonth()`, `date.dayOfYear()`, `date.weekOfMonth()`, `date.weekOfYear()`, `date.monthOfYear()`, `date.year()`

# 2.0.4
- Bugfix: Removed the filterInputsBy package line, which prevents scanning custom packages that are registered on hopebuilder

# 2.0.3
- Bugfix in `pointer.exists`
- Version bump for vulnerable components
 
# 2.0.2
- Added equals and hashCode to value types

# 2.0.1
- Reflections library shaded

# 2.0
- Bulk evaluation api added
    - Additional api to return index of first matching rule
- Json Path and Pointer pre-compilation
- Loop perf optimization
- Added plugin to commit benchmarks as part of PR
- Set JDK version to 11

# 1.1.2
- Reflections version bump

# 1.1.1
- Fixed parser issue in escaped string parsing

# 1.1.0

- Supported field reference using json pointers
- Supported quotation using single quotes
- Jackson version bump

