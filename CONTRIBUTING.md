# Contributing to Grit

Thank you for your interest in contributing to Grit! We appreciate your support.

## How to Contribute
1. **Fork the Repository:** Fork the repository to your own GitHub account.
2. **Create a Branch:** Create a new branch for your feature or bug fix from `dev`. Use a descriptive name, such as `feat/add-new-feature` or `fix/resolve-issue-123`.
3. **Make Your Changes:** Make your changes to the codebase.
4. **Submit a Pull Request:** Once you're ready, submit a pull request from your branch to the `dev` branch of the main repository. Please reference the issue you created in your pull request description.

## Pull Request Guidelines
*   Ensure your code follows the existing style and conventions.
*   Write clear and concise commit messages.
*   Make sure your changes are well-tested.

I'll review your pull request as soon as possible. Thank you for your contribution!

## Overview of modules
* `:app:` : Builds the android apk, includes additional ui for settings
* `:shared:core` : All presentation logic for the app, including habits and tasks ui and shared common components 
* `:webdemo:` : Builds the web demo which is deployed by CI on push to master. Also can be used as hot reload
```shell
./gradlew :webdemo:hotRunJvm --auto
```

## Compose Guidelines
* Expose a `Modifier` for each composable
* Write Previews for significant composables (components, etc.), Make it a private function in the same file as the composable
* Run spotless `./gradlew spotlessApply` before making a commit