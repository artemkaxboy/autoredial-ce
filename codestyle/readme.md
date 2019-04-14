# Code style settings

The `google-java-format` plugin is the preferred way to format the
code. As it only kicks in on demand, it’s also recommended to have code
style settings which help to create properly formatted code as-you-go.
Those settings can’t completely mimic the format enforced by the
`google-java-format` plugin but try to be as close as possible.
 So before submitting code, please make sure to run Reformat Code.

1. Download [intellij-java-google-style.xml](https://raw.githubusercontent.com/google/styleguide/gh-pages/intellij-java-google-style.xml).

2. Go to **File → Settings → Editor → Code Style**.

3. Click on **Manage**.

4. Click on **Import**.

5. Choose `IntelliJ IDEA Code Style XML`.

6. Select the previously downloaded file
`intellij-java-google-style.xml`.

7. Make sure that `Google Style` is chosen as **Scheme**.

[Source](https://gerrit-review.googlesource.com/Documentation/dev-intellij.html#_code_style_settings)
