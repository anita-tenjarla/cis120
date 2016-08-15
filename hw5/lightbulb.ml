(* Lightbulb example using checkboxes. *)
;; open Widget
;; open Gctx

(* Make a lightbulb widget controlled by a checkbox's state. *)
let mk_state_lightbulb () : widget =

  let (switch_w, switch_cb) =
     Widget.checkbox false "STATE LIGHT" in

  (* A function to display the bulb  *)
  let paint_bulb (g:gctx) : unit =
    let g_new = Gctx.with_color g
            (if switch_cb.get_value ()
             then Gctx.yellow
             else Gctx.black) in
       Gctx.fill_rect g_new (0, 99) (99, 99)
  in

  let (bulb, _) = Widget.canvas (100,100) paint_bulb
  in
    Widget.hpair bulb switch_w

(* Make a lightbulb that registers an item listener with the checkbox *)
let mk_listener_lightbulb () : widget =
  let is_on = ref false in

  let (switch_w, switch_cb) =
     Widget.checkbox false "LISTENER LIGHT" in

   switch_cb.add_change_listener (fun b -> is_on := b);

  (* A function to display the bulb  *)
   let paint_bulb (g:gctx) : unit =
     let g_new = Gctx.with_color g
            (if !is_on
             then Gctx.yellow
             else Gctx.black) in
       Gctx.fill_rect g_new (0, 99) (99, 99)
   in

   let (bulb, _) = Widget.canvas (100,100) paint_bulb
   in
    Widget.hpair bulb switch_w

(* Make the quit button *)
let (quit_w, _, qnc) = Widget.button "QUIT"
let qf () : unit =
   exit 0
;; qnc.add_event_listener (mouseclick_listener qf)

(* Put the lightbulb next to the quit button *)

let w = Widget.hpair (Widget.border (mk_state_lightbulb ()))
         (Widget.hpair (Widget.space (10,10))
                          (Widget.hpair (Widget.border (mk_listener_lightbulb ()))
                                           (Widget.hpair (Widget.space (10, 10)) (Widget.border (quit_w)))))

(** Run the event loop to process user events. *)
;; Eventloop.run w
