# canvas-demos

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Play

Change the data in `canvas-demos.drawing/drawing` and see the pictures change
onscreen.

To play an animation, pass a seq of frames to
`canvas-demos.drawing/animate!`. Frames get drawn on
window.requestAnimationFrame. There's currently no frame dropping or other
fancy features.

## Production Build

This is a series of proof of concepts. Don't run it in production.

## License

Copyright © 2017 Thomas Getgood

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
