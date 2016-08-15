(** Paint application *)

;; open Gctx
;; open Widget

(******************************************)
(**    SHAPES, MODES and PROGRAM STATE    *)
(******************************************)

(** The paint program uses the mutable record (called [state] below)
to store its state.  *)

(** A location in the paint_canvas widget *)
type point = Gctx.position (* from Gctx *)

(** The shapes that are visible in the paint canvas -- these make up the
    picture that the user has drawn, as well as any other "visible" elements
    that must show up in the canvas area (e.g. a "selection rectangle"). At
    the start of the homework, the only available shape is a line.  *)
(* TODO: You will modify this definition in Tasks 2, 4, 5 and maybe 6. *)

type shape = 
  | Line of Gctx.color * int * point * point
  | Points of Gctx.color * point list 
  | Ellipse of Gctx.color * int * point * int * int

(** These are the possible interaction modes that the paint program might be
    in. Some interactions require two modes. For example, the GUI might
    recognize the first mouse click as starting a line and a second mouse
    click as finishing the line.

    To start out, there are only two modes:

    - LineStartMode means the paint program is waiting for the user to make
    the first click to start a line.

    - LineEndMode means that the paint program is waiting for the user's
    second click. The point associated with this mode stores the location of
    the user's first mouse click.  *)
(* TODO: You will need to modify this type in Tasks 2, 4, and maybe 6. *)
type mode = 
  | LineStartMode
  | LineEndMode of point
  | PointMode
  | EllipseStartMode
  | EllipseEndMode of point

(** The state of the paint program. *)
type state = {
  (** The sequence of all shapes drawn by the user, in order from
  least recent (the head) to most recent (the tail). *)
  shapes : shape Deque.deque;

  (** The input mode the Paint program is in. *)
  mutable mode : mode;

  (** The currently selected pen color. *)
  mutable color : color;
  
  (**preview line*) 
  mutable preview : shape option;
  
  (**line thickness*)
  mutable thickness : int; 

  (* TODO: You will need to add new state for Tasks 3, 5, and *)
  (* possibly 6 *) 
}

(** Initial values of the program state. *)
let paint : state = {
  shapes = Deque.create ();
  mode = LineStartMode;
  color = black;
  preview = None; 
  thickness = 1; 
  (* TODO: You will need to add new state for Tasks 3, 5, and maybe 6 *)
  
}



(** This function creates a graphics context with the appropriate
    pen color.
*)
(* TODO: Your will need to modify this function in Task 5 *)
let with_params (g: gctx) (c: color) (t: int) : gctx =
  let g = with_thickness (with_color g c) t in
  g

(*********************************)
(** PAINT CANVAS REPAINTING      *)
(*********************************)
(** The paint_canvas repaint function.

    This function iterates through all the drawn shapes (in order of least
    recent to most recent so that they are layered on top of one another
    correctly) and uses the Gctx.draw_xyz functions to display them on the
    canvas.  *)
    
(* TODO: You will need to modify this repaint function in Tasks 2, 3, 4,   *)
(* and possibly 6. For example, if the user is performing some operation   *)
(* that provides "preview" (see Task 2) the repaint function must also     *)
(* show the preview.                                                       *)
let repaint (g: gctx) : unit =
  let draw_shape (s: shape) : unit =
    begin match s with
      | Line (c, t, p1, p2) -> draw_line (with_params g c t) p1 p2
      | Points (c, ps) -> draw_points (with_color g c) (ps)
      | Ellipse (c, t, ps, rx, ry) -> draw_ellipse (with_params g c t) 
      ps rx ry
    end in
  Deque.iterate draw_shape paint.shapes; 
  
  begin match paint.preview with
    | None -> ()
    | Some p -> draw_shape p
  end 

(** Create the actual paint_canvas widget and its associated
notifier_controller . *)
let ((paint_canvas : widget), (paint_canvas_controller : 
notifier_controller)) =
  canvas (600, 350) repaint

(************************************)
(**  PAINT CANVAS EVENT HANDLER     *)
(************************************)

(** The paint_action function processes all events that occur 
    in the canvas region. *)
(* TODO: Tasks 2, 3, 4, 5, and 6 involve changes to paint_action. *)
let paint_action (gc:gctx) (event:event) : unit =
  let p  = event_pos event gc in  (* mouse position *)
  begin match (event_type event) with
    | MouseDown ->
        begin match paint.mode with 
          | LineStartMode ->                                                    
                paint.mode <- LineEndMode p
          | LineEndMode p1 ->
                Deque.insert_tail (Line (paint.color, paint.thickness, 
                p1, p)) paint.shapes;
                paint.preview <- None; 
                paint.mode <- LineStartMode
         | PointMode -> 
                paint.preview <- Some (Points (paint.color, [p]))
         | EllipseStartMode -> 
                paint.mode <- EllipseEndMode p
         | EllipseEndMode e -> 
                ()
        end
    | MouseDrag ->
       begin match paint.mode with
       | LineStartMode -> ()
       | LineEndMode pp -> paint.preview <- Some (Line (paint.color, 
         paint.thickness, pp, p))
       | PointMode -> 
           let points = 
           begin match paint.preview with 
           | None -> []
           | Some (Points (_, ps)) -> ps
           | Some (Line (_, _, _, _)) -> []
           | Some (Ellipse (_, _, _, _, _)) -> []
           end in 
       paint.preview <- Some (Points (paint.color, points @ [p]))
      | EllipseStartMode -> ()
      | EllipseEndMode pp -> 
        let xc = (fst p + fst pp) / 2 in 
        let yc = (snd p + snd pp) / 2 in 
        let rx = abs (fst p - fst pp) in 
        let ry = abs (snd p - snd pp) in 
        
        paint.preview <- Some (Ellipse (paint.color, paint.thickness, 
        (xc, yc), rx, ry)) 
       end      
    | MouseUp ->
      begin match paint.mode with
      | LineStartMode -> ()
      | LineEndMode pp -> 
        paint.preview <- None; 
        Deque.insert_tail (Line (paint.color, paint.thickness, pp, p)) 
        paint.shapes;
        paint.mode <- LineStartMode
      | PointMode -> 
          let points = 
            begin match paint.preview with
            | None -> []
            | Some (Points (_, ps)) -> ps
            | Some (Line (_, _, _, _)) -> []
            | Some (Ellipse (_, _, _, _, _)) -> []
            end in
          paint.preview <- None; 
        begin match points with 
        | [] -> ()
        | pl -> Deque.insert_tail (Points (paint.color, points)) paint.shapes
        end
     | EllipseStartMode -> ()
     | EllipseEndMode pp ->       
         let xc = (fst p + fst pp) / 2 in 
         let yc = (snd p + snd pp) / 2 in 
         let rx = abs (fst p - fst pp) in 
         let ry = abs (snd p - snd pp) in 
         
        Deque.insert_tail (Ellipse (paint.color, paint.thickness, 
        (xc, yc), rx, ry)) paint.shapes;
        paint.mode <- EllipseStartMode
      end
    | _ -> 
      begin match paint.mode with
      | LineStartMode -> ()
      | LineEndMode pp -> paint.preview <- 
      Some (Line (paint.color, paint.thickness, pp, p))
      | PointMode -> ()
      | EllipseStartMode -> ()
      | EllipseEndMode pp -> ()
      end
  end

(** Add the paint_action function as a listener to the paint_canvas *)
;; paint_canvas_controller.add_event_listener paint_action

(**************************************)
(** TOOLBARS AND PAINT PROGRAM LAYOUT *)
(**************************************)

(**
This part of the program creates the other widgets for the
paint program -- the buttons, color selectors, etc., and
lays them out in the top - level window.

*)
(* TODO: Tasks 1, 2, 4, 5, and 6 involving adding new buttons or changing  *)
(* the layout of the Paint GUI. Initially the layout is very ugly because  *)
(* we use only the hpair widget demonstrated in Lecture. Task 1 is to make *)
(* improvements to make the layout more appealing. You may choose to       *)
(* arrange the buttons and other GUI elements of the paint program however *)
(* you like (so long as it is easily apparent how to use the interface ).  *)
(* The sample screen shot of our solution provides one possible design.    *)
(* Also, feel free to improve the visual components of the GUI, for        *)
(* example, our solution puts borders around the buttons and uses a custom *)
(* "color button" that changes its appearance based on whether or not the  *)
(* color is currently selected.                                            *)

(** Create the Undo button *)
let (w_undo, lc_undo, nc_undo) = button "Undo" 

(**
This function runs when the Undo button is clicked.
It simply removes the last shape from the shapes deque.
*)
(* TODO: You need to modify this in Task 3 and 4. *)
let undo () : unit =
  if Deque.is_empty paint.shapes then () 
  else ignore (Deque.remove_tail paint.shapes);
  
  paint.preview <- None;
  
  begin match paint.mode with
  | LineStartMode -> ()
  | LineEndMode pp -> paint.mode <- LineStartMode
  | PointMode -> ()
  | EllipseStartMode -> ()
  | EllipseEndMode pp -> paint.mode <- EllipseStartMode 
  end
       
;; nc_undo.add_event_listener (mouseclick_listener undo)

(** The Quit button, with associated functionality. *)
let w_quit, lc_quit, nc_quit = button "Quit"
;; nc_quit.add_event_listener (mouseclick_listener (fun () -> exit 0))

(*Lines mode button*)
let w_Lines, lc_Lines, nc_Lines = button "Line"
;; nc_Lines.add_event_listener (mouseclick_listener 
(fun () -> paint.mode <- LineStartMode))

(*Points mode button*)
let w_Points, lc_Points, nc_Points = button "Point"
;; nc_Points.add_event_listener (mouseclick_listener 
(fun () -> paint.mode <- PointMode))

(*Ellipse mode button*)
let w_Ellipse, lc_Ellipse, nc_Ellipse = button "Ellipse"
;; nc_Ellipse.add_event_listener (mouseclick_listener 
(fun () -> paint.mode <- EllipseStartMode))

(** A spacer widget *)
let spacer : widget = space (10,10)

(*thickness controller*)
let w_thickness, vc_thickness = checkbox false "Thick_Lines"
;; vc_thickness.add_change_listener (fun (x: bool) ->
  if x then paint.thickness <- 5 else paint.thickness <- 1)

(*thickness bar*) 
let w_tbar, vc_tbar = tbar 0 "Thickness_Bar" paint.thickness
;; vc_tbar.add_change_listener (fun (x: int) -> paint.thickness <- x)

(** The mode toolbar, initially containing just the Undo and Quit buttons. *)
(*  TODO: you will need to add more buttons to the toolbar in *)
(*  Tasks 2,5, and 6. *)
let mode_toolbar : widget = Widget.hlist [spacer; w_undo; spacer; w_quit; 
  spacer; w_Lines; spacer; w_Points; spacer; w_Ellipse; spacer; 
  w_thickness; spacer; w_tbar]


(* The color selection toolbar. *)
(* This toolbar contains an indicator for the currently selected color 
   and some buttons for changing it. Both the indicator and the buttons 
   are small square widgets built from this higher-order function. *)
(** Create a widget that displays itself as colored square with the given 
    width and color specified by the [get_color] function. *)
let colored_square (width:int) (get_color:unit -> color)
    : widget * notifier_controller =
  let repaint_square (gc:gctx) =
   let c = get_color () in
    fill_rect (with_color gc c) (0, width-1) (width-1, width-1) in   
  canvas (width,width) repaint_square

(** The color_indicator repaints itself with the currently selected 
   color of the paint application. *)
let color_indicator =
  let indicator,_ = colored_square 24 (fun () -> paint.color) in
  let lab, _ = label "Current Color" in
  border (hpair lab indicator)

(** color_buttons repaint themselves with whatever color they were created 
   with. They are also installed with a mouseclick listener
   that changes the selected color of the paint app to their color. *)  
let color_button (c: color) : widget =
  let w,nc = colored_square 10 (fun () -> c) in
  nc.add_event_listener (mouseclick_listener (fun () ->
    paint.color <- c ));
  w
(** The color selection toolbar. Contains the color indicator and 
    buttons for several different colors. *)
let color_toolbar : widget =
  Widget.hlist [spacer; color_indicator; spacer; color_button black; 
  spacer; color_button white; spacer; color_button red; spacer; 
  color_button green; spacer; color_button blue; spacer; 
  color_button yellow; spacer; color_button cyan; spacer; 
  color_button magenta]


(** The top-level paint program widget: a combination of the
    mode_toolbar, the color_toolbar and the paint_canvas widgets.
*)
(* TODO: Task 1 (and others) modify the layout to add new buttons and make *)
(* the layout more aesthetically appealing.                                *)

let paint_widget =
  Widget.vlist [paint_canvas; spacer; mode_toolbar; spacer; color_toolbar]

(**************************************)
(** Start the application             *)
(**************************************)

(** Run the event loop to process user events. *)
;; Eventloop.run paint_widget
