# Canvas Demos

This is a sandbox to play with ideas about new graphical programming languages.

## Experiments

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

## Issues

This is very much a work in progress. List of weirdness follows:

* Width of lines doesn't scale with zoom. This means that if you create a circle
  with stroke-width 10px and then zoom out, it eventually becomes a disc, no
  matter how large the radius originally was. This means that some style scalars
  actually need to be transformed with the projection logic. Didn't think of
  that before...

## Development Mode

### Run application:

```
lein clean
lein figwheel dev
```

Figwheel will automatically push cljs changes to the browser.

Personally I basically never run `lein clean`, but the I'm not about to change
the docs.

Wait a bit, then browse to [http://localhost:3449](http://localhost:3449).

## Play

Set `canvas-demos.core/main` to the `draw!` or `start!` function from any of the
namespaces in the `canvas-demos.examples` and figwheel will load the new demo as
soon as it compiles.

Play around with the code defining the picture to see how the drawing works, add
new types of shapes, or anything you want.

## References

These aren't all my ideas obviously. Proper references will be coming soon.

## License

Copyright © 2017 Thomas Getgood

Distributed under the Eclipse Public License either version 1.0 or (at your
option) any later version.
