# Canvas Demos

This is a sandbox to play with ideas about new graphical programming languages.

## Goals

The creation of a graphics programing language that lets one program graphics
and possibly even UIs in a simple, lisp like fashion. In particular it should
be:

* Declarative: images are just data that gets passed to a renderer
* Stateless: composition of shapes in itself should not change the properties of
  the constituent parts
* Immediate Mode: no retained state between renders
* Uniform: manipulation of composite images should use the same functions as the
  manipulation of their parts
* Intuitive where it can be, well reasoned where it can't: convention is not an
  excuse for gotchas

N.B.: This project is only concerned currently with 2d vector graphics using
HTML canvas. I would like to extend that to webgl and even opengl on the jvm
eventually, but one thing at a time.

## How to Use it

### Start Application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

### Play

My own dev workflow uses the `canvas-demos.core/switch!` function to switch the
var curently being rendered to the screen. It's important that it be a var so
that changes reloaded by figwheel trigger a rerender of the canvas.

Look in the table `canvas-demos.core/var-table` for a list of existing
toys. Some are more interesting than others, but they all show different ways to
use the language.

In particular look at `:election` --- a datagraphic summarising the results of
the last several Canadian Federal elections --- and `:presentation` --- a
slideshow about this library written using this library --- which is the
default. To run the slideshow as a slideshow run
`(canvas-demos.core/presentation!)`. Use `j` and `k` to move forward and back
through the slides.

Once you've loaded a picture, go ahead and play around with the code defining it
to see how the drawing works, add new types of shapes, or anything you want.

If you pass a sequence of images to `canvas-demos.drawing/animate!` it will play
the sequence of images one per animation frame. Basic animations are simple via
`(map #(translate x % 0) (range))`. Compositing images can be done as with
`canvas-demos.examples.ex1/composite` (no special ns exists as yet for animation
support).

## Ideas

### Shapes as data

Shapes are just data (currently records, though that may change) along the lines
of

```clojure
#canvas-demos.drawings.base.Line{:from [0 0] :to [1000 1000] :style {:stroke :red}}
```

The `canvas-demos.shapes.base` namespace contains convenience functions to
create lines, circles, rectangles, and pixels (pixels are just squares of side
length 1, not actual pixels, thus they respond to affine transformations just
like everything else).

All shapes have a `:style` key which defines the styles applied to the
shape. Part of statelessness is that the global state of canvas is wrapped so
that shape styles don't interfere. This mostly works, but there are still a few
quirks around paths that can cause weird behaviour. If you see something, say
something.

### Affine Transformations

Affine Transformations are the natural compositing mechanism for vector
graphics. They allow composite shapes to be manipulated in the same fashion as
individual path segments.

See the `canvas-demos.shapes.affine` namespace for details. Currently there are
helper functions for translation, rotation, scaling, and reflection. You can
easily make your own as well if you see fit.

### Infinite Canvas

By default, panning and scrolling make the canvas respond as does your favourite
maps program. This makes exploring images quick and fun. Beyond that I'm
positive it's useful, just give me time to figure out how to explain that.

## Experimental Directions

This section is old, and some of these have been shelved. Due for an update.

#### Stateless Declarative API to HTML Canvas

The statefulness of canvas is possibly its most annoying feature. Find a sprite
you like that's open source, cut and paste it into your code, and all of a
sudden there are gradients, or dotted lines, or something else that you didn't
think to guard against all over your picture.

By stateless I mean that each thing you draw contains all of its styling info in
an encapsulated fashion. The runtime takes care of all the annoying guard code.

By declarative, I mean that I want to talk about polylines, polygons, bezier
curves, et al. as types of shapes that take a set of points as input. I don't
want to have to worry about whether the pen is on the paper or not. So no
`moveTo` vs `lineTo`, no `stroke`, just `(line {:from [10 30] :to [200 670]
:style {...}})`, or something like that.

The `canvas-demos.examples.ex1` ns is a simple demonstration of this idea.

#### Animations

Animation should be as trivial as creating a vector of static images. Just
create one image per frame and the runtime you play it like a flipbook. With
lazy seqs these animations can go on indefinitely without taking much ram.

`canvas-demos.examples.ex2` contains a basic demo of animations --- and an
interesting but awkwardly implemented way to benchmark performance.

#### A Window into a Picture

How hard is it to use the canvas as a window into a larger (and potentially
unbounded) picture? Pan and zoom across a picture or animation as it plays just
like in your favourite maps site.

All examples use this by default.

#### Build Pictures from Pictures

If I draw a snail, and then I draw a flower, and then I want to make a garden
full of flowers covered in snails, how hard should it be to just copy and move,
rotate, shrink, etc. the previous pictures to make a new whole? Dead simple I
contend.

#### Draw <==> Code

What if a simplified paint like program, instead of just drawing a picture,
generated code to draw the picture?

What if instead of looking in a document to see what sort of basic shapes you
can draw, and what arguments they expect, and what kind of styles you can give
them, you were just presented with a drawing palette, and a styling palette, as
in Gimp or Photoshop, and as you drew, the code that you would have written to
make the picture is generated for you?

What if you could then cut and paste that code and embed it in any web page?

What might happen?

Great things.

#### Edit Whatever

For me, it's easier to draw the rough outline and then fine tune the details by
setting values by hand (finger?). If you want to rotate an image, you should be
able to click on it and rotate it like in Illustrator, or you should be able to
wrap it in a rotation transformer of some sort. That way if you hand rotate it
to 47°, and then a little more and you're at 43°, instead of giving yourself
carpal tunnel trying to get it right, you can just click on the code and type in
45°. Of course the editor could snap to "nice" presets, but every once in a
while that's really annoying and I'd rather it not. Is that a good reason to do
away with it?

Graphical editing of the image should modify the code the same way you would
have had you been doing it manually. That way you don't have to google "how do I
skew a circle in X", like every other language. This way it's self documenting. Like
Emacs. But real.

I don't yet know how I'm going to generate nice, human friendly code. That's a
fun thing to think about. Possibly won't be fun to actually hammer out, but
that's just how it goes.

#### Virtual Dommy Thing via Canvas

I don't know how to express this clearly, but in order to get the above, we need
to set up event handling on canvas so that we can know what's being clicked on,
dragged, etc.. At that point we can start to use the canvas element like a bona
fide immediate mode DOM.

That kind of sounds like a terrible idea: replace the dom with widgets drawn on
a canvas. But what happens when we start using webGL to render those widgets?
Remember what personal websites looked like in the 90's?

## References

These aren't all my ideas obviously. Proper references will be coming soon.

## License

Copyright © 2017 Thomas Getgood

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
