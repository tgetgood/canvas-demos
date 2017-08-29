# canvas-demos

## Experiments

Simple declarative and composable drawing and animation.

Use lazy seqs to render or animate infinite data streams.

Pan and zoom of the canvas as a window into the cartesian plane as a whole.

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

## References

[Example Mandlebrot viewer](https://github.com/ztellman/penumbra/blob/master/test/example/gpgpu/mandelbrot.clj)

## License

Copyright © 2017 Thomas Getgood

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
