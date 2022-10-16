# re-frame-tic-tac-toe

![CI workflow](https://github.com/jackdbd/undici/actions/workflows/ci.yaml/badge.svg)

Tic tac toe implemented as a [re-frame](https://github.com/day8/re-frame) app.

Live at https://re-frame-tic-tac-toe.pages.dev/

## Installation

This project can be managed with [Babashka tasks](https://book.babashka.org/#tasks) defined in a `bb.edn` file. You can check the list of available tasks using this command:

```sh
bb tasks
```

Install all dependencies:

```sh
bb install
```

## Development

Compile the application in watch mode with shadow-cljs:

```sh
bb dev
```

The command above will open browser tabs for:

- Re-frame app: http://localhost:8280/
- Shadown CLJS dashboard: http://localhost:9630/dashboard

Tip: you can compile any shadow-cljs build IDs directly from the browser, without typing anything in the terminal. Just visit http://localhost:9630/builds to do it.

## Build

Compile an optimized release build of the app:

```sh
npx shadow-cljs release app

# or use this babashka task
bb app:release
```

Run the app in your default browser:

```sh
xdg-open resources/public/index.html
```

Generate a build report for the re-frame app:

```sh
npx shadow-cljs run shadow.cljs.build-report app target/build-report.html

# or use this babashka task
bb app:release-report
```

Inspect the build report in your default browser:

```sh
# linux
xdg-open target/build-report.html

# macos
open target/build-report.html

# windows
cmd /c start target/build-report.html
```

## Tests

```sh
bb test:browser
```

```sh
bb test:karma
npx karma start --config karma.conf.js --reporters verbose,dots
```

## Deploy

The app is deployed to Cloudflare Pages by the [CI workflow](./.github/workflows/ci.yaml), using [Direct Upload with a GitHub Action](https://developers.cloudflare.com/pages/how-to/use-direct-upload-with-continuous-integration/#use-github-actions).

If you want to deploy the app manually, you can run use [wrangler2](https://github.com/cloudflare/wrangler2):

```sh
npx wrangler pages publish resources/public --project-name=$CLOUDFLARE_PAGES_PROJECT_ID
```

You can also use this babashka task if you want to save a few keystrokes:

```sh
bb app:deploy
```

npx shadow-cljs watch app --verbose
