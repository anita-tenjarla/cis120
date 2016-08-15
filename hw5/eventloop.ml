(** The main eventloop of the GUI library *)

(* Do not modify this file. *)

open Widget

  
(**
This function takes a widget, which is the "root" of the GUI interface.
It creates the "top-level" Gctx, and then it goes into an infinite loop.
The loop simply repeats these steps forever:
- clear the graphics window
- ask the widget to repaint itself
- wait for a user input event
- forward the event to the widget's event handler
*)
let run (w: widget) : unit =
  let g = Gctx.top_level in         (* the top-level Gctx *)
  Gctx.open_graphics ();
  let rec loop () =
    Graphics.clear_graph ();
    w.repaint g;
    Graphics.synchronize ();           (* show the painted window *)
    let e = Gctx.wait_for_event () in  (* wait for a user input event *)
    w.handle g e; loop ()              (* let widget handle the event *)
  in
  loop ()
