package com.devdaily.opensource.jelly;

import java.awt.*;
import java.awt.event.*;
import java.util.prefs.*;

import javax.swing.*;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;
import java.io.*;
import java.lang.reflect.InvocationTargetException;

import javax.swing.text.*;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;
import javax.swing.border.Border;
import javax.swing.event.*;

import com.devdaily.opensource.jelly.model.LatexFileFactory;
import com.devdaily.opensource.jelly.view.*;
import java.util.*;
import com.devdaily.opensource.jelly.controller.EditActions;
import com.devdaily.opensource.jelly.controller.HyperlinkDialogController;
import com.devdaily.opensource.jelly.controller.IndexTreeController;
import com.devdaily.opensource.jelly.controller.OpenRecentFileController;
import com.devdaily.opensource.jelly.controller.RecentFilesMenuController;
import com.devdaily.opensource.jelly.util.HTMLStringUtils;
import com.devdaily.opensource.jelly.util.StringUtils;
import com.ibm.was.graphics.RolloverIcon;

import java.awt.dnd.DropTarget;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;

import java.awt.event.KeyEvent;

//public class MainFrame extends JFrame
// KeyListener is for displaying the recent file list w/o having a corresonding menu item
public class MainFrame 
extends AniSheetableJFrame
implements KeyListener
{
  private final static String APP_NAME = "WikiStar";
  
  // recent file list
  RecentFilesMenuController recentFilesMenuController;
  JMenu recentFilesMenu;
  private static final String RECENT_FILE_LIST_FILENAME = ".wikistarRecentFilelist";
  java.util.List recentFileList;
  File recentFileListFile;
  OpenRecentFileController openRecentFileController;
  
  // drag-drop stuff
  private DropTarget dropTarget;

  // hyperlink dialog
  HyperlinkDialogController hyperlinkDialogController;

  // curtain
  CurtainComponent transparentComponent;
  JFrame curtainFrame;
  MainFrame mainFrame;
  boolean curtainIsShowing = false;

  // file-chooser stuff
  String currentDirectory = "";

  // save the frame size info as preferences
  Preferences prefs;
  private String THE_X            = "X";
  private String THE_Y            = "Y";
  private String THE_HEIGHT       = "HEIGHT";
  private String THE_WIDTH        = "WIDTH";
  private String WIKISTAR_WKG_DIR = "WIKISTAR_WKG_DIR";

  JPanel mainContentPane;
  JMenuBar menuBar = new JMenuBar();
  JMenu fileMenu = new JMenu();
  JMenuItem fileExitMenuItem = new JMenuItem();
  JMenu helpMenu = new JMenu();
  JMenuItem help_aboutMenuItem = new JMenuItem();
  JMenuItem help_legalFormattingCharacters = new JMenuItem();

  // kludge to handle window-minimizing on mac
  JMenu windowMenu = new JMenu();
  JMenuItem minimizeWindowMenuItem = new JMenuItem();

  // the Text menu
  JMenu textMenu = new JMenu();
  JMenuItem textMenu_boldMenuItem = new JMenuItem();
  JMenuItem textMenu_codeMenuItem = new JMenuItem();
  JMenuItem textMenu_italicsMenuItem = new JMenuItem();
  JMenuItem textMenu_underlineMenuItem = new JMenuItem();
  JMenuItem textMenu_hyperlinkMenuItem = new JMenuItem();

  JToolBar toolBar = new JToolBar();
  JButton btnHyperlink = new JButton();
  JButton btnFileOpen = new JButton();
  JButton btnFileSave = new JButton();
  JButton btnHelp = new JButton();
  JToggleButton btnToggleCurtain = new JToggleButton();
  JToggleButton wordWrapToggleButton = new JToggleButton();

  // text-formatting buttons
  JButton btnBold = new JButton();
  JButton btnCode = new JButton();
  JButton btnItalics = new JButton();
  JButton btnUnderline = new JButton();
  ImageIcon imgBoldText;
  ImageIcon imgCodeText;
  ImageIcon imgItalicsText;
  ImageIcon imgUnderlineText;

  ImageIcon imgHyperlink;
  ImageIcon imgFileOpen;
  ImageIcon imgFileSave;
  ImageIcon imgHelp;
  ImageIcon imgWordWrap;
  ImageIcon imgCurtainToggle;
  JLabel statusBar = new JLabel();
  BorderLayout borderLayout1 = new BorderLayout();

  // fields for the main ui
  JScrollPane rgaEditorScrollPane = new JScrollPane();
  JScrollPane latexViewScrollPane = new JScrollPane();
  JScrollPane htmlViewScrollPane = new JScrollPane();
  JScrollPane htmlPreviewScrollPane = new JScrollPane();

  JTextArea rgaEditingArea = new JTextArea();
  JTextArea latexViewEditorPane = new JTextArea();
  JTextArea htmlViewEditorPane = new JTextArea();

  JEditorPane htmlPreviewPane = new JEditorPane("text/html", "");

  JTabbedPane rhsTabbedPane = new JTabbedPane();
  
  private JPanel toolBarPanel = new JPanel();
  private FlowLayout toolBarPanelLayout = new FlowLayout(FlowLayout.CENTER);


  // add a listener to make the screen occupy the entire window
  MakeFullScreenListener makeFullScreenListener;

  //todo Difference here between JEditorPane and JTextPane???
  //JEditorPane rgaEditingArea = new JEditorPane();
  //JTextPane rgaEditingArea = new JTextPane();

  JPopupMenu jPopupMenu1 = new JPopupMenu();
  JMenu characterMenu = new JMenu();
  JMenu documentMenu = new JMenu();
  JMenu sectionMenu = new JMenu();
  JMenu paragraphMenu = new JMenu();
  JMenuItem docStyleMenuItem = new JMenuItem();
  JMenuItem docTitleMenuItem = new JMenuItem();
  JMenuItem fileOpenMenuItem = new JMenuItem();
  JMenuItem fileSaveMenuItem = new JMenuItem();
  JMenuItem fileSaveAsMenuItem = new JMenuItem();
  JMenu editMenu = new JMenu();
  JMenuItem editCutMenuItem = new JMenuItem();
  JMenuItem editCopyMenuItem = new JMenuItem();
  JMenuItem editPasteMenuItem = new JMenuItem();

  // @todo - need to switch to the old chooser so this will look better on the mac
  //java.awt.FileDialog jFileChooser1 = new java.awt.FileDialog(this);
  JFileChooser jFileChooser1 = new JFileChooser();
  FileDialog fileDialog = new FileDialog(this);

  String currFileName = null;  // Full path with filename. null means new/untitled.
  boolean dirty = false;
  JMenuItem jMenuItem1 = new JMenuItem();
  JMenuItem noindentMenuItem = new JMenuItem();
  Document rgaDocument;
  JMenuItem adsenseMenuItem = new JMenuItem();
  JMenuItem appendixMenuItem = new JMenuItem();
  JMenuItem chapterMenuItem = new JMenuItem();
  JMenuItem paragraphMenuItem = new JMenuItem();
  JMenuItem sectionMenuItem = new JMenuItem();
  JMenuItem subsectionMenuItem = new JMenuItem();
  JMenuItem subsubsectionMenuItem = new JMenuItem();
  JMenuItem tocMenuItem = new JMenuItem();
  JMenuItem titleMenuItem = new JMenuItem();
  JMenuItem italicsMenuItem = new JMenuItem();
  JMenuItem underlineMenuItem = new JMenuItem();
  JMenuItem ellipsisMenuItem = new JMenuItem();
  JMenu jMenu1 = new JMenu();
  JMenuItem enumerateMenuItem = new JMenuItem();
  JMenuItem itemizeMenuItem = new JMenuItem();
  JMenu fontMenu = new JMenu();
  JMenu fontStyleMenu = new JMenu();
  JMenuItem romanFontStyle = new JMenuItem();
  JMenuItem sansSerifFontStyle = new JMenuItem();
  JMenuItem typewriterFontStyle = new JMenuItem();
  JMenuItem boldMenuItem = new JMenuItem();
  JMenuItem ttMenuItem = new JMenuItem();
  JMenu fontSizeMenu = new JMenu();
  JMenuItem tinyFontSize = new JMenuItem();
  JMenuItem scriptFontSize = new JMenuItem();
  JMenuItem footnoteFontSize = new JMenuItem();
  JMenuItem smallFontSize = new JMenuItem();
  JMenuItem normalFontSize = new JMenuItem();
  JMenuItem littleLargeFontSize = new JMenuItem();
  JMenuItem normalLargeFontSize = new JMenuItem();
  JMenuItem bigLargeFontSize = new JMenuItem();
  JMenuItem littleHugeFontSize = new JMenuItem();
  JMenuItem bigHugeFontSize = new JMenuItem();
  JMenuItem emphMenuItem = new JMenuItem();
  JMenu specialMenu = new JMenu();
  JMenu specialBeginEndMenu = new JMenu();
  JMenuItem beginEndComment = new JMenuItem();
  JMenuItem beginEndQuote = new JMenuItem();
  JMenuItem beginEndVerbatim = new JMenuItem();
  JMenuItem beginEndVerse = new JMenuItem();
  JMenuItem beginEndTabular = new JMenuItem();
  JMenu jMenu2 = new JMenu();
  JMenuItem newArticleMenuItem = new JMenuItem();
  JMenuItem newBookMenuItem = new JMenuItem();
  JMenuItem newReportMenuItem = new JMenuItem();
  JMenuItem newSlidesMenuItem = new JMenuItem();
  JMenuItem jMenuItem6 = new JMenuItem();
  JMenuItem helpListOfCommandsMenuItem = new JMenuItem();
  JMenuItem boldfaceMenuItem = new JMenuItem();
  JMenuItem textnormalMenuItem = new JMenuItem();
  JMenuItem emphasizedMenuItem = new JMenuItem();
  JMenuItem fontstyleItalicsMenuItem = new JMenuItem();
  JMenuItem fontstyleMediumMenuItem = new JMenuItem();
  JMenuItem fontstyleSlantedMenuItem = new JMenuItem();
  JMenuItem fontstyleSmallCapsMenuItem = new JMenuItem();
  JMenuItem fontstyleUprightMenuItem = new JMenuItem();
  JMenu jMenu3 = new JMenu();
  JMenuItem tabularMenuItem = new JMenuItem();
  JMenuItem listDescriptionMenuItem = new JMenuItem();

  JMenuItem edit_FindMenuItem = new JMenuItem();
  JMenuItem edit_PopupMenuMenuItem = new JMenuItem();
  JMenuItem edit_fontMenuItem = new JMenuItem();
  JMenuItem edit_undoMenuItem = new JMenuItem();

  int currentRow = 0;
  int currentCol = 0;

  java.util.List stuffSearchForPreviously = new ArrayList();

  private static final int TAB_KEY = 9;

  // UNDO/REDO CAPABILITY
  /**
   * Listener for the edits on the current document.
   */
  protected UndoableEditListener undoHandler = new UndoHandler();

  /** UndoManager that we add edits to. */
  protected UndoManager undoManager = new UndoManager();

  private UndoAction undoAction = null;
  private RedoAction redoAction = null;
  private String TAB_AS_SPACES = "   ";

  // mainSplitPane configuration parameters
  private int splitPaneDividerLocation = 150;
  private final int splitPaneDividerSize = 10;
  private final boolean splitPaneOneTouchExpandable = true;
  private IndexTreeController indexTreeController;


  /**Construct the frame*/
  public MainFrame()
  {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try
    {
      jbInit();
      setupCurtainFrame();
    }
    catch(Exception e)
    {
      e.printStackTrace();
    }
  }

  
  //----------------------------- HYPERLINK CODE BEGINS HERE ---------------------------------//
  
  public void doHyperlinkUpdateTextAction()
  {
    // WORKING HERE
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\texttt{", selectedText, "}");
  }
  
  public JTextArea getRgaEditingArea()
  {
    return rgaEditingArea;
  }

  
  //----------------------------- CURTAIN CODE BEGINS HERE ---------------------------------//
  
  private void setupCurtainFrame()
  {
    mainFrame = this;
    SwingUtilities.invokeLater(new Runnable() 
    {
      public void run() {
        // this is the black undecorated frame
        curtainFrame = new JFrame();
        curtainFrame.setUndecorated(true);
        curtainFrame.setBackground(Color.BLACK);
        curtainFrame.getContentPane().setBackground(Color.BLACK);
        curtainFrame.addMouseListener(new CurtainMouseListener());  
        transparentComponent = new CurtainComponent(curtainFrame);
        curtainFrame.getContentPane().add("Center", transparentComponent);
        curtainFrame.pack();
        makeFrameFullSize(curtainFrame);
        // this was needed so the mainFrame would get on top
        // after the black frame rolled out
//        mainFrame.setAlwaysOnTop(true);
      }
    });
  }

  private void makeFrameFullSize(JFrame aFrame) {
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    aFrame.setSize(screenSize.width, screenSize.height);
  }
  
  private void hideCurtain()
  {
    mainFrame.setAlwaysOnTop(false);
    transparentComponent.turnOff();
    mainFrame.requestFocus(true);
    curtainIsShowing = false;
  }

  class CurtainButtonOnListener implements ActionListener {
    public void actionPerformed(ActionEvent ae) 
    {
      if (curtainIsShowing)
      {
        // hide it
        hideCurtain();
      }
      else
      {
        // show it
        mainFrame.setAlwaysOnTop(true);
        transparentComponent.turnOn();
        mainFrame.requestFocus(true);
        curtainIsShowing = true;
      }
    }
  }

  class CurtainMouseListener implements MouseListener 
  {
    public void mouseClicked(MouseEvent arg0) {
      hideCurtain();
    }
    public void mousePressed(MouseEvent arg0) { }
    public void mouseReleased(MouseEvent arg0) { }
    public void mouseEntered(MouseEvent arg0) { }
    public void mouseExited(MouseEvent arg0) { }
  }
  
  
  //----------------------------- PREFERENCES CODE BEGINS HERE ---------------------------------//
  
  private void usePreferencesForInit() throws NumberFormatException {
    String sX = prefs.get(THE_X, "0");
    String sY = prefs.get(THE_Y, "0");
    String sHeight = prefs.get(THE_HEIGHT, "400");
    String sWidth = prefs.get(THE_WIDTH, "600");
    int theX = Integer.parseInt(sX);
    int theY = Integer.parseInt(sY);
    int theHeight = Integer.parseInt(sHeight);
    int theWidth = Integer.parseInt(sWidth);
    this.setLocation(theX,theY);
    this.setSize(theWidth,theHeight);

    // set the current directory based on any previously-set preference, i.e.,
    // from the last time the application was used
    currentDirectory = prefs.get(WIKISTAR_WKG_DIR, null);
  }

  /**Component initialization*/
  private void jbInit() throws Exception
  {
    hyperlinkDialogController = new HyperlinkDialogController(this);
    openRecentFileController = new OpenRecentFileController(this);

    imgBoldText = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("text_bold.png"));
    imgCodeText = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("text_normal.png"));
    imgItalicsText = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("text_italics.png"));
    imgUnderlineText = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("text_underlined.png"));

    imgHyperlink = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("link.png"));
    imgFileOpen = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("folder.png"));
    imgFileSave = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("disk_blue.png"));
    imgHelp = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("help2.png"));
    imgWordWrap = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("word-wrap-1.png"));
    imgCurtainToggle = new ImageIcon(com.devdaily.opensource.jelly.MainFrame.class.getResource("presentation.png"));

    //setIconImage(Toolkit.getDefaultToolkit().createImage(MainFrame.class.getResource("[Your Icon]")));
    mainContentPane = (JPanel) this.getContentPane();
    mainContentPane.setLayout(borderLayout1);

    rgaDocument = rgaEditingArea.getDocument();

    // used for preferences support
    this.addComponentListener(new MainFrame_componentAdapter(this));

    this.setSize(new Dimension(400, 300));
    this.setTitle(APP_NAME);
    statusBar.setText(" ");
    fileMenu.setText("File");
    fileExitMenuItem.setText("Exit");
    fileExitMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileExitMenuItem_actionPerformed(e);
      }
    });

    windowMenu.setText("Window");
    minimizeWindowMenuItem.setText("Minimize Window");
    minimizeWindowMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        minimizeWindowMenuItem_actionPerformed(e);
      }
    });
    minimizeWindowMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    // ------------- TEXT MENU --------------
    textMenu.setText("Text");
    textMenu_boldMenuItem.setText("Bold");
    textMenu_boldMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textMenu_boldMenuItem_actionPerformed(e);
      }
    });
    textMenu_boldMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    textMenu_codeMenuItem.setText("Code");
    textMenu_codeMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textMenu_codeMenuItem_actionPerformed(e);
      }
    });
    textMenu_codeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    textMenu_italicsMenuItem.setText("Italics");
    textMenu_italicsMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textMenu_italicsMenuItem_actionPerformed(e);
      }
    });
    textMenu_italicsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    textMenu_underlineMenuItem.setText("Underline");
    textMenu_underlineMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textMenu_underlineMenuItem_actionPerformed(e);
      }
    });
    textMenu_underlineMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    
    textMenu_hyperlinkMenuItem.setText("Hyperlink");
    textMenu_hyperlinkMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textMenu_hyperlinkMenuItem_actionPerformed(e);
      }
    });
    textMenu_hyperlinkMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    
    helpMenu.setText("Help");
    help_aboutMenuItem.setText("About");
    help_aboutMenuItem.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        helpAboutMenuItem_actionPerformed(e);
      }
    });

    help_legalFormattingCharacters.setText("Legal Formatting Characters");
    help_legalFormattingCharacters.addActionListener(new ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        help_legalFormattingCharacters_actionPerformed(e);
      }
    });


    
    btnBold.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        actionPerformed_boldTextButton(e);
      }
    });
    configureButtonAppearance(btnBold, imgBoldText, "Bold");

    btnCode.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        actionPerformed_codeTextButton(e);
      }
    });
    configureButtonAppearance(btnCode, imgCodeText, "Code");

    btnItalics.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        actionPerformed_italicsTextButton(e);
      }
    });
    configureButtonAppearance(btnItalics, imgItalicsText, "Italics");

    btnUnderline.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        actionPerformed_underlineTextButton(e);
      }
    });
    configureButtonAppearance(btnUnderline, imgUnderlineText, "Underline");

    btnHyperlink.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        actionPerformed_hyperlinkButton(e);
      }
    });
    configureButtonAppearance(btnHyperlink, imgHyperlink, "Create Hyperlink");

    btnFileOpen.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileOpenJButton_actionPerformed(e);
      }
    });
    configureButtonAppearance(btnFileOpen, imgFileOpen, "Open File");

    // put wordWrapToggleButton here
    wordWrapToggleButton.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        wordWrapToggleButton_actionPerformed(e);
      }
    });
    configureButtonAppearance(wordWrapToggleButton, imgWordWrap, "Enable Word Wrap");

    btnFileSave.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileSaveJButton_actionPerformed(e);
      }
    });
    configureButtonAppearance(btnFileSave, imgFileSave, "Save File");

    btnHelp.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        helpJButton_actionPerformed(e);
      }
    });
    configureButtonAppearance(btnHelp, imgHelp, "Help");

    btnToggleCurtain.addActionListener(new CurtainButtonOnListener());
//    btnToggleCurtain.addActionListener(new java.awt.event.ActionListener()
//    {
//      public void actionPerformed(final ActionEvent e)
//      {
//        actionPerformed_buttonToggleCurtain(e);
//      }
//    });
    configureButtonAppearance(btnToggleCurtain, imgCurtainToggle, "Toggle the curtain");

//    rgaEditingArea.setText("\\documentclass[a4paper,11pt]{article}\n" +
//                        "\\author{Alvin Alexander}\n" +
//                        "\\title{Making a Java Application Look Like a Native Mac Application}\n" +
//                        "\\begin{document}\n" +
//                        "\\maketitle\n" +
//                        "\\tableofcontents\n\n" +
//                        "\\end{document}");
    rgaEditingArea.setText("");
    rgaEditingArea.setMargin(new Insets(5, 5, 5, 5));
    //rgaEditingArea.setFont(new java.awt.Font("Monospaced", 0, 11));
    //rgaEditingArea.setFont(new Font("Serif",Font.PLAIN,8));
    rgaEditingArea.setFont(new java.awt.Font("Monaco", 0, 12));

    latexViewEditorPane.setMargin(new Insets(5, 5, 5, 5));
    //latexViewEditorPane.setBorder(BorderFactory.createEtchedBorder());
    latexViewEditorPane.setBackground(new Color(244,244,244));
    latexViewEditorPane.setEditable(false);
    latexViewEditorPane.setFont(new java.awt.Font("Monaco", 0, 12));
    
    //@todo - refactor
    htmlViewEditorPane.setMargin(new Insets(5, 5, 5, 5));
    htmlViewEditorPane.setEditable(false);
    htmlViewEditorPane.setBackground(new Color(244,244,244));
    htmlViewEditorPane.setFont(new java.awt.Font("Monaco", 0, 12));

    rgaEditingArea.addCaretListener(new javax.swing.event.CaretListener()
    {
      public void caretUpdate(CaretEvent e)
      {
        rgaEditorPane_caretUpdate(e);
      }
    });
    rgaEditingArea.addKeyListener(new java.awt.event.KeyAdapter()
    {
      public void keyPressed(KeyEvent e)
      {
        rgaEditorPane_keyPressed(e);
      }
    });
    rgaEditingArea.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(MouseEvent e)
      {
        rgaEditorPane_mousePressed(e);
      }
    });
    characterMenu.setText("Character");
    documentMenu.setText("Document");
    sectionMenu.setText("Section");
    paragraphMenu.setText("Paragraph");
    docStyleMenuItem.setText("Style");
    docStyleMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        docStyleMenuItem_actionPerformed(e);
      }
    });
    docTitleMenuItem.setText("Title");
    docTitleMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        docTitleMenuItem_actionPerformed(e);
      }
    });
    fileOpenMenuItem.setText("Open...");
    //fileOpenMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(79, java.awt.event.KeyEvent.CTRL_MASK, false));
    fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    fileOpenMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileOpenMenuItem_actionPerformed(e);
      }
    });
    fileSaveMenuItem.setText("Save");
    //fileSaveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_S, java.awt.event.KeyEvent.CTRL_MASK, false));
    fileSaveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    fileSaveMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileSaveMenuItem_actionPerformed(e);
      }
    });
    fileSaveAsMenuItem.setText("Save As...");
    fileSaveAsMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fileSaveAsMenuItem_actionPerformed(e);
      }
    });
    editMenu.setText("Edit");
    editCutMenuItem.setEnabled(false);
    editCutMenuItem.setText("Cut");
    //editCutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(88, java.awt.event.KeyEvent.CTRL_MASK, false));
    editCutMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    editCopyMenuItem.setEnabled(false);
    editCopyMenuItem.setText("Copy");
    //editCopyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(67, java.awt.event.KeyEvent.CTRL_MASK, false));
    editCopyMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    editPasteMenuItem.setEnabled(false);
    editPasteMenuItem.setText("Paste");
    //editPasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(86, java.awt.event.KeyEvent.CTRL_MASK, false));
    editPasteMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    // IMPLEMENTING UNDO
    undoAction = new UndoAction();
    editMenu.add(undoAction);

    // crazy stuff here to TEMPORARILY add a Ctrl-Z action listener to
    // an additional undo menu item (until I figure out how to
    // do this properly)
    edit_undoMenuItem.setText("Undo");
    //edit_undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(90, java.awt.event.KeyEvent.CTRL_MASK, false));
    edit_undoMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
    edit_undoMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        undoAction.actionPerformed(e);
      }
    });
    editMenu.add(edit_undoMenuItem);

    redoAction = new RedoAction();
    editMenu.add(redoAction);


    jMenuItem1.setText("\\indent");
    jMenuItem1.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        jMenuItem1_actionPerformed(e);
      }
    });
    noindentMenuItem.setText("\\noindent");
    noindentMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        noindentMenuItem_actionPerformed(e);
      }
    });

    rgaDocument.addDocumentListener(new javax.swing.event.DocumentListener()
    {
      public void insertUpdate(DocumentEvent e)
      {
        document1_insertUpdate(e);
      }
      public void removeUpdate(DocumentEvent e)
      {
        document1_removeUpdate(e);
      }
      public void changedUpdate(DocumentEvent e)
      {
        document1_changedUpdate(e);
      }
    });
    adsenseMenuItem.setText("<--adsense-->");
    adsenseMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        adsenseMenuItem_actionPerformed(e);
      }
    });
    appendixMenuItem.setText("Appendix");
    appendixMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        appendixMenuItem_actionPerformed(e);
      }
    });
    chapterMenuItem.setText("Chapter");
    chapterMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        chapterMenuItem_actionPerformed(e);
      }
    });
    paragraphMenuItem.setText("Paragraph");
    paragraphMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        paragraphMenuItem_actionPerformed(e);
      }
    });
    sectionMenuItem.setText("Section");
    sectionMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        sectionMenuItem_actionPerformed(e);
      }
    });
    subsectionMenuItem.setText("Subsection");
    subsectionMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        subsectionMenuItem_actionPerformed(e);
      }
    });
    subsubsectionMenuItem.setText("Subsubsection");
    subsubsectionMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        subsubsectionMenuItem_actionPerformed(e);
      }
    });
    tocMenuItem.setText("Table of contents");
    tocMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        tocMenuItem_actionPerformed(e);
      }
    });
    titleMenuItem.setText("Title");
    titleMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        titleMenuItem_actionPerformed(e);
      }
    });
    italicsMenuItem.setText("Italics");
    italicsMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        italicsMenuItem_actionPerformed(e);
      }
    });
    underlineMenuItem.setText("Underline");
    underlineMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        underlineMenuItem_actionPerformed(e);
      }
    });
    ellipsisMenuItem.setText("...");
    ellipsisMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        ellipsisMenuItem_actionPerformed(e);
      }
    });
    jMenu1.setText("Lists");
    enumerateMenuItem.setText("Enumerate");
    enumerateMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        enumerateMenuItem_actionPerformed(e);
      }
    });
    itemizeMenuItem.setText("Itemize");
    itemizeMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        itemizeMenuItem_actionPerformed(e);
      }
    });
    fontMenu.setText("Font");
    fontStyleMenu.setText("Style");
    romanFontStyle.setText("Roman");
    romanFontStyle.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        romanFontStyle_actionPerformed(e);
      }
    });
    sansSerifFontStyle.setText("Sans Serif");
    sansSerifFontStyle.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        sansSerifFontStyle_actionPerformed(e);
      }
    });
    typewriterFontStyle.setText("Typewriter");
    typewriterFontStyle.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        typewriterFontStyle_actionPerformed(e);
      }
    });
    boldMenuItem.setText("Bold");
    boldMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        boldMenuItem_actionPerformed(e);
      }
    });
    ttMenuItem.setText("Typewriter");
    ttMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        ttMenuItem_actionPerformed(e);
      }
    });
    fontSizeMenu.setText("Size");
    tinyFontSize.setText("Tiny");
    tinyFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        tinyFontSize_actionPerformed(e);
      }
    });
    scriptFontSize.setText("Scriptsize");
    scriptFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        scriptFontSize_actionPerformed(e);
      }
    });
    footnoteFontSize.setText("Footnotesize");
    footnoteFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        footnoteFontSize_actionPerformed(e);
      }
    });
    smallFontSize.setText("Small");
    smallFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        smallFontSize_actionPerformed(e);
      }
    });
    normalFontSize.setText("Normal");
    normalFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        normalFontSize_actionPerformed(e);
      }
    });
    littleLargeFontSize.setText("large");
    littleLargeFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        littleLargeFontSize_actionPerformed(e);
      }
    });
    normalLargeFontSize.setText("Large");
    normalLargeFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        normalLargeFontSize_actionPerformed(e);
      }
    });
    bigLargeFontSize.setText("LARGE");
    bigLargeFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        bigLargeFontSize_actionPerformed(e);
      }
    });
    littleHugeFontSize.setText("huge");
    littleHugeFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        littleHugeFontSize_actionPerformed(e);
      }
    });
    bigHugeFontSize.setText("Huge");
    bigHugeFontSize.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        bigHugeFontSize_actionPerformed(e);
      }
    });
    emphMenuItem.setText("Emphasized");
    emphMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        emphMenuItem_actionPerformed(e);
      }
    });
    specialMenu.setText("Special");
    specialBeginEndMenu.setText("begin...end");
    beginEndComment.setText("Comment");
    beginEndComment.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        beginEndComment_actionPerformed(e);
      }
    });
    beginEndQuote.setText("Quote");
    beginEndQuote.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        beginEndQuote_actionPerformed(e);
      }
    });
    beginEndVerbatim.setText("Verbatim");
    beginEndVerbatim.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        beginEndVerbatim_actionPerformed(e);
      }
    });
    beginEndVerse.setText("Verse");
    beginEndVerse.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        beginEndVerse_actionPerformed(e);
      }
    });
    beginEndTabular.setText("Tabular");
    beginEndTabular.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        beginEndTabular_actionPerformed(e);
      }
    });
    jMenu2.setText("New...");
    newArticleMenuItem.setText("Article");
    newArticleMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        newArticleMenuItem_actionPerformed(e);
      }
    });
    newBookMenuItem.setText("Book");
    newBookMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        newBookMenuItem_actionPerformed(e);
      }
    });
    newReportMenuItem.setText("Report");
    newReportMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        newReportMenuItem_actionPerformed(e);
      }
    });
    newSlidesMenuItem.setText("Slides");
    newSlidesMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        newSlidesMenuItem_actionPerformed(e);
      }
    });
    jMenuItem6.setEnabled(false);
    jMenuItem6.setText("Reserved Characters");
    helpListOfCommandsMenuItem.setText("List of Commands");
    helpListOfCommandsMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        helpListOfCommandsMenuItem_actionPerformed(e);
      }
    });
    boldfaceMenuItem.setText("Bold");
    boldfaceMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        boldfaceMenuItem_actionPerformed(e);
      }
    });
    textnormalMenuItem.setText("Document (textnormal)");
    textnormalMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        textnormalMenuItem_actionPerformed(e);
      }
    });
    emphasizedMenuItem.setText("Emphasized");
    emphasizedMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        emphasizedMenuItem_actionPerformed(e);
      }
    });
    fontstyleItalicsMenuItem.setText("Italics");
    fontstyleItalicsMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fontstyleItalicsMenuItem_actionPerformed(e);
      }
    });
    fontstyleMediumMenuItem.setText("Medium");
    fontstyleMediumMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fontstyleMediumMenuItem_actionPerformed(e);
      }
    });
    fontstyleSlantedMenuItem.setText("Slanted");
    fontstyleSlantedMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fontstyleSlantedMenuItem_actionPerformed(e);
      }
    });
    fontstyleSmallCapsMenuItem.setText("Small Caps");
    fontstyleSmallCapsMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fontstyleSmallCapsMenuItem_actionPerformed(e);
      }
    });
    fontstyleUprightMenuItem.setText("Upright");
    fontstyleUprightMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        fontstyleUprightMenuItem_actionPerformed(e);
      }
    });
    jMenu3.setText("Table");
    tabularMenuItem.setText("Tabular");
    tabularMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        tabularMenuItem_actionPerformed(e);
      }
    });
    listDescriptionMenuItem.setText("Description (insert only)");
    listDescriptionMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        listDescriptionMenuItem_actionPerformed(e);
      }
    });
    edit_FindMenuItem.setMnemonic('0');
    edit_FindMenuItem.setText("Find");
    //edit_FindMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(70, java.awt.event.KeyEvent.CTRL_MASK, false));
    edit_FindMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    edit_FindMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        edit_FindMenuItem_actionPerformed(e);
      }
    });
    // "Edit | Popup Menu"
    edit_PopupMenuMenuItem.setText("Menu (popup)");
    //edit_PopupMenuMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(77, java.awt.event.KeyEvent.CTRL_MASK, false));
    edit_PopupMenuMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));

    edit_PopupMenuMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        edit_PopupMenuMenuItem_actionPerformed(e);
      }
    });

    // "Edit | Font"
    edit_fontMenuItem.setText("Change Font");
    //edit_fontMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(77, java.awt.event.KeyEvent.CTRL_MASK, false));
    edit_fontMenuItem.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(final ActionEvent e)
      {
        edit_fontMenuItem_actionPerformed(e);
      }
    });

    rhsTabbedPane.addChangeListener(new javax.swing.event.ChangeListener()
    {
      public void stateChanged(ChangeEvent e)
      {
        jTabbedPane_stateChanged(e);
      }
    });
    toolBar.setAlignmentX(JToolBar.CENTER_ALIGNMENT);
    
    // separator looks good on windows
    //fileMenu.addSeparator();
    fileMenu.add(jMenu2);
    fileMenu.addSeparator();
    fileMenu.add(fileOpenMenuItem);
    fileMenu.addSeparator();
    fileMenu.add(fileSaveMenuItem);
    fileMenu.add(fileSaveAsMenuItem);
    fileMenu.addSeparator();

    // HERE1
    recentFilesMenuController = new RecentFilesMenuController(this);
    recentFilesMenu = recentFilesMenuController.getRecentFilesMenu();
    // TODO Need to add menu here
    fileMenu.add(recentFilesMenu);
    
    fileMenu.add(fileExitMenuItem);
    
    editMenu.addSeparator();
    editMenu.add(edit_FindMenuItem);
    editMenu.addSeparator();
    editMenu.add(editCutMenuItem);
    editMenu.add(editCopyMenuItem);
    editMenu.add(editPasteMenuItem);
    editMenu.addSeparator();
    editMenu.add(edit_PopupMenuMenuItem);
    editMenu.addSeparator();
    editMenu.add(edit_fontMenuItem);

    textMenu.add(textMenu_boldMenuItem);
    textMenu.add(textMenu_codeMenuItem);
    textMenu.add(textMenu_italicsMenuItem);
    textMenu.add(textMenu_underlineMenuItem);
    textMenu.add(textMenu_hyperlinkMenuItem);

    windowMenu.add(minimizeWindowMenuItem);

    helpMenu.add(help_aboutMenuItem);
    helpMenu.add(helpListOfCommandsMenuItem);
    helpMenu.add(help_legalFormattingCharacters);
    helpMenu.add(jMenuItem6);
    
    menuBar.add(fileMenu);
    menuBar.add(editMenu);
    menuBar.add(textMenu);
    menuBar.add(windowMenu);
    menuBar.add(helpMenu);
    this.setJMenuBar(menuBar);
    
    configureToolBar();
    
    mainContentPane.add(toolBarPanel, BorderLayout.NORTH);
    
    // TODO getting rid of status bar for now
    //mainContentPane.add(statusBar, BorderLayout.SOUTH);
    mainContentPane.add(rhsTabbedPane, BorderLayout.CENTER);
    
    rhsTabbedPane.add(rgaEditorScrollPane, "Wiki Editor");
    rhsTabbedPane.add(latexViewScrollPane, "LaTeX");
    rhsTabbedPane.add(htmlViewScrollPane, "HTML");
    rhsTabbedPane.add(htmlPreviewScrollPane, "Preview");

    // @todo: The HTML View needs to be coded/implemented
    latexViewScrollPane.getViewport().add(latexViewEditorPane, null);
    rgaEditorScrollPane.getViewport().add(rgaEditingArea, null);
    htmlViewScrollPane.getViewport().add(htmlViewEditorPane, null);
    htmlPreviewScrollPane.getViewport().add(htmlPreviewPane, null);
    
    // for mac only:
    rgaEditorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    rgaEditorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

    jPopupMenu1.add(adsenseMenuItem);

    jPopupMenu1.add(documentMenu);
    jPopupMenu1.add(sectionMenu);
    jPopupMenu1.add(paragraphMenu);
    jPopupMenu1.add(characterMenu);
    jPopupMenu1.add(jMenu1);
    jPopupMenu1.add(specialMenu);
    jPopupMenu1.add(jMenu3);
    documentMenu.add(docStyleMenuItem);
    documentMenu.add(docTitleMenuItem);

    paragraphMenu.add(jMenuItem1);
    paragraphMenu.add(noindentMenuItem);
    sectionMenu.add(appendixMenuItem);
    sectionMenu.add(chapterMenuItem);
    sectionMenu.add(paragraphMenuItem);
    sectionMenu.add(sectionMenuItem);
    sectionMenu.add(subsectionMenuItem);
    sectionMenu.add(subsubsectionMenuItem);
    sectionMenu.add(tocMenuItem);
    sectionMenu.add(titleMenuItem);
    characterMenu.add(boldMenuItem);
    characterMenu.add(ttMenuItem);
    characterMenu.add(emphMenuItem);
    characterMenu.add(italicsMenuItem);
    characterMenu.add(underlineMenuItem);
    characterMenu.add(ellipsisMenuItem);
    characterMenu.add(fontMenu);
    jMenu1.add(listDescriptionMenuItem);
    jMenu1.add(enumerateMenuItem);
    jMenu1.add(itemizeMenuItem);
    fontMenu.add(fontStyleMenu);
    fontMenu.add(fontSizeMenu);
    fontStyleMenu.add(romanFontStyle);
    fontStyleMenu.add(sansSerifFontStyle);
    fontStyleMenu.add(typewriterFontStyle);
    fontStyleMenu.addSeparator();
    fontStyleMenu.add(boldfaceMenuItem);
    fontStyleMenu.add(textnormalMenuItem);
    fontStyleMenu.add(emphasizedMenuItem);
    fontStyleMenu.add(fontstyleItalicsMenuItem);
    fontStyleMenu.add(fontstyleMediumMenuItem);
    fontStyleMenu.add(fontstyleSlantedMenuItem);
    fontStyleMenu.add(fontstyleSmallCapsMenuItem);
    fontStyleMenu.add(fontstyleUprightMenuItem);
    fontSizeMenu.add(tinyFontSize);
    fontSizeMenu.add(scriptFontSize);
    fontSizeMenu.add(footnoteFontSize);
    fontSizeMenu.add(smallFontSize);
    fontSizeMenu.add(normalFontSize);
    fontSizeMenu.add(littleLargeFontSize);
    fontSizeMenu.add(normalLargeFontSize);
    fontSizeMenu.add(bigLargeFontSize);
    fontSizeMenu.add(littleHugeFontSize);
    fontSizeMenu.add(bigHugeFontSize);
    specialMenu.add(specialBeginEndMenu);
    specialBeginEndMenu.add(beginEndComment);
    specialBeginEndMenu.add(beginEndQuote);
    specialBeginEndMenu.add(beginEndTabular);
    specialBeginEndMenu.add(beginEndVerbatim);
    specialBeginEndMenu.add(beginEndVerse);
    jMenu2.add(newArticleMenuItem);
    jMenu2.add(newBookMenuItem);
    jMenu2.add(newReportMenuItem);
    jMenu2.add(newSlidesMenuItem);
    jMenu3.add(tabularMenuItem);
    // set the caret actively in the document

    // note -- seems best if this is the last thing done, otherwise
    // it was throwing an exception, probably because i am adding
    // default text to the document
    rgaDocument.addUndoableEditListener(undoHandler);

    this.setSize(Toolkit.getDefaultToolkit().getScreenSize());

    // start adding functionality to display editor in the entire screen
    // this currently listens for the CTRL-F11 keystroke while in the rgaTextEditorArea
    makeFullScreenListener = new MakeFullScreenListener(this);
    rgaEditingArea.registerKeyboardAction(makeFullScreenListener,
                                   KeyStroke.getKeyStroke(122, java.awt.event.KeyEvent.CTRL_MASK, false),
                                   JComponent.WHEN_IN_FOCUSED_WINDOW);

    rgaEditingArea.setCaretPosition(0);
    //@todo: i'm not getting initial focus here
    rgaEditingArea.setFocusable(true);
    this.pack();
    rgaEditingArea.requestFocus();
    rgaEditingArea.requestFocus(true);
    rgaEditingArea.requestFocusInWindow();

    // preferences setup
    prefs = Preferences.userNodeForPackage(this.getClass());
    usePreferencesForInit();
    
    enableWordWrapByDefault();
    
    // setting up the toolbarpanel as a drop target
    dropTarget = new DropTarget(toolBarPanel,new DropTargetImplementation());

    // try adding key listener to the main frame to support opening the recent files dialog
    // without having to add a corresponding menu item
    // TODO may have to add this to the frame and other editing areas as well
    rgaEditingArea.addKeyListener(this);
  } // end of jbinit

  
  protected void textMenu_hyperlinkMenuItem_actionPerformed(ActionEvent e)
  {
    actionPerformed_hyperlinkButton(null);
  }

  //============ drag an drop configuration starts  ==============//

  private class DropTargetImplementation extends DropTargetAdapter
  {
    public void drop(DropTargetDropEvent dtde)
    {
      try
      {
        dtde.acceptDrop(DnDConstants.ACTION_LINK);
        java.util.List dndThings = (java.util.List)dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
        dtde.dropComplete(true);
        processFiles(dndThings);
      }
      catch (Exception e)
      {
        dtde.rejectDrop();
      }
    }

  } // end of DropTargetImpl class

  private void processFiles(java.util.List listOfThings)
  {
    Thread thread = new Thread(new ProcessFilesRunnable(listOfThings));
    thread.start();
  }

  private class ProcessFilesRunnable implements Runnable
  {
    private java.util.List listOfFiles;

    public ProcessFilesRunnable(java.util.List files)
    {
      this.listOfFiles = files;
    }

    public void run()
    {
      // note: may be a list of files; just open the first one for now
      final File file = (File) listOfFiles.get(0);
      final String fullFilename = file.getAbsolutePath();

      try
      {
        SwingUtilities.invokeAndWait(new Runnable()
        {
          public void run()
          {
            quickFileOpen(fullFilename);
          }
        });
      }
      catch (InterruptedException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (InvocationTargetException e)
      {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  } // end of ProcessFilesRunnable  
  
  //============ drag an drop configuration ends  ==============//
  

  private void configureToolBar()
  {
    // some Mac-specific toolbar things (worked well with tiger)
//    toolBarPanelLayout.setHgap(6);
//    toolBarPanelLayout.setVgap(18);
//    toolBarPanel.setLayout(toolBarPanelLayout);

    // leopard: b/c i can't set the button border to null i've compensated this way
    toolBarPanelLayout.setHgap(-4);
    toolBarPanelLayout.setVgap(18);
    toolBarPanel.setLayout(toolBarPanelLayout);

    toolBarPanel.add(btnFileSave);
    toolBarPanel.add(btnFileOpen);
    //toolBarPanel.add(wordWrapToggleButton);
    toolBarPanel.add(btnToggleCurtain);
    toolBarPanel.add(btnHelp);
    toolBarPanel.add(btnHyperlink);
    
    // leopard: added this line along with other changes
    //          to get the button spacing to look right
    toolBarPanel.add(Box.createHorizontalStrut(28));

    // leopard: i used several toolbars to achieve the desired spacing effect in 
    //          tiger, but replaced them with the Box and horizontal strut in leopard
    //toolBarPanel.add(new JToolBar.Separator());
    
    //@todo -- a better way to achieve this spacing?
    toolBarPanel.add(btnBold);
    toolBarPanel.add(btnCode);
    toolBarPanel.add(btnItalics);
    toolBarPanel.add(btnUnderline);
    
    // this creates an interesting effect (more spacing on top)
    //toolBar.setMargin(new Insets(22,22,22,22));
    //toolBarPanel.add(toolBar);
    toolBar.setFloatable(false);
    
    //toolBar.setRollover(true);
    //toolBar.setOpaque(true);
    //toolBar.setMargin(new Insets(5,5,5,5));
  }

  private void enableWordWrapByDefault()
  {
    wordWrapToggleButton.setSelected(true);
    wordWrapToggleButton_actionPerformed(null);
  }
  
  private void configureButtonAppearance(AbstractButton button, ImageIcon imageIcon, String rolloverText)
  {
    button.setToolTipText(rolloverText);
    button.setIcon(imageIcon);
    button.setRolloverEnabled(true);
    button.setRolloverIcon(new RolloverIcon(imageIcon));
    
    // this no longer works on leopard (boo-hoo!)
    // huge effect - this gets rid of the border entirely, leaving only the image
    //button.setBorder(null);
    //button.setBorder()
    
    // leopard: compensating b/c setBorder(null) does not work in leopard (added next two lines)
    button.setContentAreaFilled(false);
    button.setBorderPainted(false);

    button.setOpaque(false);
  }

  private void refreshTreeButton_actionPerformed(final ActionEvent e)
  {
    // get locations of "\\chapter" in the rgaEditorArea
    indexTreeController.updateTree();
  }

  public void fileExitMenuItem_actionPerformed(final ActionEvent e)
  {
    if ( okToAbandon() )
    {
      System.exit(0);
    }
  }

  public void minimizeWindowMenuItem_actionPerformed(final ActionEvent e)
  {
    //System.err.println("TRYING TO FIRE WINDOW CLOSING EVENT");
    //mainFrame.dispatchEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSED));
    //mainFrame.processWindowEvent(new WindowEvent(mainFrame, WindowEvent.WINDOW_CLOSED));
    mainFrame.setExtendedState(Frame.ICONIFIED);
  }

  /**Help | About action performed*/
  public void helpAboutMenuItem_actionPerformed(final ActionEvent e)
  {
    MainFrame_AboutBox dlg = new MainFrame_AboutBox(this);
    Dimension dlgSize = dlg.getPreferredSize();
    Dimension frmSize = getSize();
    Point loc = getLocation();
    dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
    dlg.setModal(true);
    dlg.show();
  }

  // TODO another kludge here
  public java.util.List<String> getRecentFilesList()
  {
    return recentFilesMenuController.getRecentFilesList();
  }

  protected void processWindowEvent(WindowEvent e)
  {
    //super.processWindowEvent(e);
    if (e.getID() == WindowEvent.WINDOW_CLOSING)
    {
      fileExitMenuItem_actionPerformed(null);
    }
  }

  void rgaEditorPane_mousePressed(MouseEvent e)
  {
    if (e.getModifiers() == Event.META_MASK)
    {
      jPopupMenu1.show( rgaEditingArea,e.getX(),e.getY() );
    }
  }

  // Handle the File|Open menu or button, invoking okToAbandon and openFile
  // as needed.
  private void fileOpen()
  {
    if (!okToAbandon())
    {
      return;
    }

//    jFileChooser1.setFileFilter(new RGAFileFilter());
//
//    // Use the OPEN version of the dialog, test return for Approve/Cancel
//    if (JFileChooser.APPROVE_OPTION == jFileChooser1.showOpenDialog(this)) {
//      // Call openFile to attempt to load the text from file into TextArea
//      openFile(jFileChooser1.getSelectedFile().getPath());
//    }
    fileDialog.setModal(true);
    fileDialog.setMode(FileDialog.LOAD);
    fileDialog.setTitle("Open a File");
    if (currentDirectory!=null && !currentDirectory.trim().equals(""))
    {
      fileDialog.setDirectory(currentDirectory);
    }
    fileDialog.setVisible(true);
    
    // after the dialog is used ...
    if (fileDialog.getDirectory() != null)
    {
      currentDirectory = fileDialog.getDirectory();
    }
    String filename = fileDialog.getDirectory() + 
      System.getProperty("file.separator") + fileDialog.getFile();
    if (fileDialog.getFile() != null)
    {
      openFile(filename);
    }
    this.repaint();
  }

  public void quickFileOpen(String filename)
  {
    // if current file is dirty prompt to save it first
    if (okToAbandon())
    {
      openFile(filename);
    }
  }
  
  
  
  
  // Open named file; read text from file into jTextArea1; report to statusBar.
  private void openFile(String fileName)  {
    try
    {
      // Open a file of the given name.
      File file = new File(fileName);
      if (!file.exists())
      {
        JOptionPane.showMessageDialog(this, "The selected file does not exist.");
        return;
      }

      // Get the size of the opened file.
      int size = (int)file.length();

      // Set to zero a counter for counting the number of
      // characters that have been read from the file.
      int chars_read = 0;

      // Create an input reader based on the file, so we can read its data.
      // FileReader handles international character encoding conversions.
      FileReader in = new FileReader(file);

      // Create a character array of the size of the file,
      // to use as a data buffer, into which we will read
      // the text data.
      char[] data = new char[size];

      // Read all available characters into the buffer.
      while(in.ready()) {
        // Increment the count for each character read,
        // and accumulate them in the data buffer.
        chars_read += in.read(data, chars_read, size - chars_read);
      }
      in.close();

      // Create a temporary string containing the data,
      // and set the string into the JTextArea.
      rgaEditingArea.setText(new String(data, 0, chars_read));
      rgaEditingArea.setCaretPosition(0);
      rgaEditingArea.requestFocusInWindow();

      // Cache the currently opened filename for use at save time...
      this.currFileName = fileName;
      // ...and mark the edit session as being clean
      this.dirty = false;

      // Display the name of the opened directory+file in the statusBar.
      statusBar.setText("Opened "+fileName);
      
      recentFilesMenuController.addFilenameToRecentFilelist(fileName);

      // @todo FIX THIS
      updateCaption();
    }
    catch (IOException e)
    {
      statusBar.setText("Error opening "+fileName);
    }
  }
  
  boolean okToAbandon()
  {
    if (!dirty)
    {
      return true;
    }
    int value =  JOptionPane.showConfirmDialog(this,
                                               "Save changes?",
                                               APP_NAME,
                                               JOptionPane.YES_NO_CANCEL_OPTION) ;

    switch (value)
    {
       case JOptionPane.YES_OPTION:
         // yes, please save changes
         return saveFile();
       case JOptionPane.NO_OPTION:
         // no, abandon edits
         // i.e. return true without saving
         return true;
       case JOptionPane.CANCEL_OPTION:
       default:
         // cancel
         return false;
    }
  }
  
  // Save current file; handle not yet having a filename; report to statusBar.
  boolean saveFile()
  {

    // Handle the case where we don't have a file name yet.
    if (currFileName == null) {
      return saveAsFile();
    }

    try
    {
      // save the file from the rga textpane
      File file = new File (currFileName);
      FileWriter out = new FileWriter(file);
      String text = rgaEditingArea.getText();
      out.write(text);
      out.close();

// NO NEED TO DO THIS ANY MORE
//      String tmpFilename = getFilenameWithTexSuffix(currFileName);
//      file = new File (tmpFilename);
//      out = new FileWriter(file);
//      // update the latexEditorPane before getting the text from it
//      updateLatexEditorPane();
//      text = latexViewEditorPane.getText();
//      out.write(text);
//      out.close();

      this.dirty = false;
      // @todo FIX THIS
      updateCaption();
      return true;
    }
    catch (IOException e) {
      statusBar.setText("Error saving "+currFileName);
    }
    return false;
  }

  private String getFilenameWithTexSuffix(final String tmpFilename)
  {
    int decimalPosition = tmpFilename.indexOf('.');
    //System.err.println("decimalPosition: " + decimalPosition);
    String prefix = tmpFilename.substring(0,decimalPosition);
    String suffix = tmpFilename.substring(decimalPosition,tmpFilename.length());
    //System.err.println("prefix: " + prefix);
    //System.err.println("suffix: " + suffix);
    //System.err.println("texname: " + tmpFilename.substring(0,decimalPosition) + ".tex");

    return tmpFilename.substring(0,decimalPosition) + ".tex";
  }

  private String getFilenameWithoutSuffix(final String tmpFilename)
  {
    int decimalPosition = tmpFilename.indexOf('.');
    String prefix = tmpFilename.substring(0,decimalPosition);
    String suffix = tmpFilename.substring(decimalPosition,tmpFilename.length());
    return tmpFilename.substring(0,decimalPosition);
  }

  // Save current file, asking user for new destination name.
  // Report to statuBar.
  boolean saveAsFile() 
  {
    this.repaint();

    fileDialog.setModal(true);
    fileDialog.setMode(FileDialog.SAVE);
    fileDialog.setTitle("Save Your File");
    if (currentDirectory!=null && !currentDirectory.trim().equals(""))
    {
      fileDialog.setDirectory(currentDirectory);
    }
    fileDialog.setVisible(true);

    // after the dialog is used ...
    if (fileDialog.getDirectory() != null && fileDialog.getFile() != null)
    {
      currentDirectory = fileDialog.getDirectory();
      prefs.put(WIKISTAR_WKG_DIR, currentDirectory);
      // i changed the lines below b/c the mac is returning a "/" at the end of the directory;
      // not sure how this works on other platforms
      //String filename = fileDialog.getDirectory() + System.getProperty("file.separator") + fileDialog.getFile();
      currFileName = fileDialog.getDirectory() + fileDialog.getFile();
      recentFilesMenuController.addFilenameToRecentFilelist(currFileName);
      this.repaint();
      return saveFile();
    }
    else
    {
      return false;
    }
    
    // Use the SAVE version of the dialog, test return for Approve/Cancel
//    if (JFileChooser.APPROVE_OPTION == jFileChooser1.showSaveDialog(this)) {
//      // Set the current file name to the user's selection,
//      // then do a regular saveFile
//      currFileName = jFileChooser1.getSelectedFile().getPath();
//      //repaints menu after item is selected
//      this.repaint();
//      return saveFile();
//    }
//    else {
//      this.repaint();
//      return false;
//    }
  }

    // Update the caption of the application to show the filename and its dirty state.
  private void updateCaption()
  {
    String caption;

    if (currFileName == null)
    {
       // synthesize the "Untitled" name if no name yet.
       caption = "Untitled";
    }
    else
    {
      caption = currFileName;
    }

    // add a "*" in the caption if the file is dirty.
    if (dirty)
    {
      caption = "* " + caption;
    }
    caption = APP_NAME + " - " + caption;
    this.setTitle(caption);
  }

  void actionPerformed_hyperlinkButton(final ActionEvent e)
  {
    // WORKING HERE
    hyperlinkDialogController.showDialog();
  }

  void fileOpenJButton_actionPerformed(final ActionEvent e)
  {
    fileOpen();
  }

  void fileOpenMenuItem_actionPerformed(final ActionEvent e)
  {
    fileOpen();
  }

  void fileSaveJButton_actionPerformed(final ActionEvent e)
  {
    saveFile();
  }

  void fileSaveMenuItem_actionPerformed(final ActionEvent e)
  {
    saveFile();
  }

  void fileSaveAsMenuItem_actionPerformed(final ActionEvent e)
  {
    saveAsFile();
  }

  void wordWrapToggleButton_actionPerformed(final ActionEvent e)
  {
    if ( wordWrapToggleButton.isSelected() )
    {
      rgaEditingArea.setLineWrap(true);
      rgaEditingArea.setWrapStyleWord(true);
      latexViewEditorPane.setLineWrap(true);
      latexViewEditorPane.setWrapStyleWord(true);
      htmlViewEditorPane.setLineWrap(true);
      htmlViewEditorPane.setWrapStyleWord(true);
      wordWrapToggleButton.setToolTipText("Disable Word Wrap");
    }
    else
    {
      rgaEditingArea.setLineWrap(false);
      rgaEditingArea.setWrapStyleWord(false);
      latexViewEditorPane.setLineWrap(false);
      latexViewEditorPane.setWrapStyleWord(false);
      htmlViewEditorPane.setLineWrap(false);
      htmlViewEditorPane.setWrapStyleWord(false);
      wordWrapToggleButton.setToolTipText("Enable Word Wrap");
    }
  }

  void document1_changedUpdate(DocumentEvent e)
  {
    this.dirty = true;

    // found this reference while writing the mac tutorial, but i don't see
    // where it does anything.
    //rgaEditingArea.putClientProperty("windowModified", Boolean.TRUE);
    updateCaption();
  }

  void document1_insertUpdate(DocumentEvent e)
  {
    this.dirty = true;
    updateCaption();
  }

  void document1_removeUpdate(DocumentEvent e)
  {
    this.dirty = true;
    updateCaption();
  }

  void jMenuItem1_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\indent\n");
  }

  void noindentMenuItem_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\noindent\n");
  }

  void adsenseMenuItem_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("<!--adsense-->\n");
  }

  void appendixMenuItem_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\appendix\n");
  }

  void titleMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\title{", selectedText, "}");
  }

  void tocMenuItem_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\tableofcontents");
  }

  void subsubsectionMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\subsubsection{", selectedText, "}");
  }

  void subsectionMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\subsection{", selectedText, "}");
  }

  void sectionMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\section{", selectedText, "}");
  }

  private void paragraphMenuItem_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\paragraph");
  }

  private void chapterMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\chapter{", selectedText, "}");
  }

  private void listDescriptionMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    String newText = "\\begin{description}\n"
                   + " \\item[ITEM1] \\mbox{}\\\\DESCRIPTION1\n"
                   + " \\item[ITEM2] \\mbox{}\\\\DESCRIPTION2\n"
                   + "\\end{description}";
    replaceSelectionAndKeepCursor(newText);
  }

  private void replaceSelectedList(String pre, String selectedText, String linePrefix, String post)
  {
    if ( selectedText == null )
    {
      replaceSelectionAndKeepCursor(pre + post);
    }
    else
    {
      StringBuffer sb = new StringBuffer();
      StringTokenizer st = new StringTokenizer(selectedText,"\n");
      while ( st.hasMoreTokens() )
      {
        sb.append(linePrefix + st.nextToken() + "\n");
      }
      replaceSelectionAndKeepCursor(pre + sb.toString() + post);
    }
  }

  private void enumerateMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    if ( selectedText == null )
    {
      replaceSelectedText("\\begin{enumerate}\n", selectedText, " \\item\n\\end{enumerate}" );
    }
    else
    {
      replaceSelectedList("\\begin{enumerate}\n", selectedText, " \\item ", "\\end{enumerate}");
    }
  }

  private void itemizeMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    if ( selectedText == null )
    {
      replaceSelectedText("\\begin{enumerate}\n", selectedText, " \\item\n\\end{enumerate}" );
    }
    else
    {
      replaceSelectedList("\\begin{enumerate}\n", selectedText, " \\item ", "\\end{enumerate}");
    }
  }

  private void docStyleMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\documentstyle{", selectedText, "}");
  }

  private void docTitleMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\title{", selectedText, "}");
  }

  private void boldMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textbf{", selectedText, "}");
  }

  private void ttMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\texttt{", selectedText, "}");
  }

  private void emphMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    if ( selectedText == null )
    {
      replaceSelectionAndKeepCursor("\\emph{}");
    }
    else
    {
      replaceSelectedText("\\emph{", selectedText, "}");
    }
  }

  private void italicsMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    if ( selectedText == null )
    {
      replaceSelectionAndKeepCursor("\\textit{}");
    }
    else
    {
      replaceSelectedText("\\textit{", selectedText, "}");
    }
  }

  private void underlineMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    if ( selectedText == null )
    {
      replaceSelectionAndKeepCursor("\\underline{}");
    }
    else
    {
      replaceSelectedText("\\underline{", selectedText, "}");
    }
  }

  private void ellipsisMenuItem_actionPerformed(final ActionEvent e)
  {
    int caretPos = rgaEditingArea.getCaretPosition();
    replaceSelectionAndKeepCursor("\\ldots");
  }

  private void beginEndComment_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\begin{comment}\n", selectedText, "\n\\end{comment}\n");
  }

  private void beginEndQuote_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\begin{quote}\n", selectedText, "\n\\end{quote}\n");
  }

  private void beginEndTabular_actionPerformed(final ActionEvent e)
  {
    tabularMenuItem_actionPerformed(e);
  }

  private void beginEndVerbatim_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\begin{verbatim}\n", selectedText, "\n\\end{verbatim}\n");
  }

  private void beginEndVerse_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\begin{verse}\n", selectedText, "\n\\end{verse}\n");
  }

  public void replaceSelectedText(String pre, String selectedText, String post)
  {
    if ( selectedText == null )
    {
      replaceSelectionAndKeepCursor(pre + post);
    }
    else
    {
      replaceSelectionAndKeepCursor(pre + selectedText + post);
    }
  }

  private void replaceSelectionAndKeepCursor(final String newText)
  {
    rgaEditingArea.replaceSelection(newText);
    rgaEditingArea.repaint();
    rgaEditingArea.requestFocus();
  }


  //-------------------------//
  // File | New methods ...  //
  //-------------------------//

  private void createNewFileInEditor(final String fileContents)
  {
    if (!okToAbandon())
    {
      return;
    }
    rgaEditingArea.setText( fileContents );
    rgaEditingArea.requestFocus();
    rgaEditingArea.setCaretPosition(0);
  }

  //------------ Text menu actions -------------
  private void textMenu_boldMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("*", selectedText, "*");
  }

  private void textMenu_codeMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("=", selectedText, "=");
  }

  private void textMenu_italicsMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("_", selectedText, "_");
  }

  private void textMenu_underlineMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("<u>", selectedText, "</u>");
  }

  //------------ Text button actions ------------
  private void actionPerformed_boldTextButton(final ActionEvent e)
  {
    textMenu_boldMenuItem_actionPerformed(null);
  }
  
  private void actionPerformed_codeTextButton(final ActionEvent e)
  {
    textMenu_codeMenuItem_actionPerformed(null);
  }
  
  private void actionPerformed_italicsTextButton(final ActionEvent e)
  {
    textMenu_italicsMenuItem_actionPerformed(null);
  }
  
  private void actionPerformed_underlineTextButton(final ActionEvent e)
  {
    textMenu_underlineMenuItem_actionPerformed(null);
  }
  
  //------------ many more actions --------------

  private void newArticleMenuItem_actionPerformed(final ActionEvent e)
  {
    createNewFileInEditor( LatexFileFactory.getNewArticle() );
  }

  private void newBookMenuItem_actionPerformed(final ActionEvent e)
  {
    createNewFileInEditor( LatexFileFactory.getNewBook() );
  }

  private void newReportMenuItem_actionPerformed(final ActionEvent e)
  {
    createNewFileInEditor( LatexFileFactory.getNewReport() );
  }

  private void newSlidesMenuItem_actionPerformed(final ActionEvent e)
  {
    createNewFileInEditor( LatexFileFactory.getNewSlides() );
  }

  private void romanFontStyle_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textrm{", selectedText, "}");
  }

  private void sansSerifFontStyle_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\texttt\\sf{", selectedText, "}");
  }

  private void typewriterFontStyle_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\texttt{", selectedText, "}");
  }

  private void tinyFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\tiny\n");
  }

  private void scriptFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\scriptsize\n");
  }

  private void footnoteFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\footnotesize\n");
  }

  private void smallFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\small\n");
  }

  private void normalFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\normalsize\n");
  }

  private void littleLargeFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\large\n");
  }

  private void normalLargeFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\Large\n");
  }

  private void bigLargeFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\LARGE\n");
  }

  private void littleHugeFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\huge\n");
  }

  private void bigHugeFontSize_actionPerformed(final ActionEvent e)
  {
    replaceSelectionAndKeepCursor("\\Huge\n");
  }

  private void boldfaceMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textbf{", selectedText, "}");
  }

  private void textnormalMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textnormal{", selectedText, "}");
  }

  private void emphasizedMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\emph{", selectedText, "}");
  }

  private void fontstyleItalicsMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textit{", selectedText, "}");
  }

  private void fontstyleMediumMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textmd{", selectedText, "}");
  }

  private void fontstyleSlantedMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textsl{", selectedText, "}");
  }

  private void fontstyleSmallCapsMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textsc{", selectedText, "}");
  }

  private void fontstyleUprightMenuItem_actionPerformed(final ActionEvent e)
  {
    String selectedText = rgaEditingArea.getSelectedText();
    replaceSelectedText("\\textup{", selectedText, "}");
  }

  private void helpJButton_actionPerformed(final ActionEvent e)
  {
    helpAboutMenuItem_actionPerformed(e);
  }

  private void helpListOfCommandsMenuItem_actionPerformed(final ActionEvent e)
  {
    //JDialog helpDialog = new LatexCommandReferenceDialog(this,"LaTeX Command Reference",false);
    JFrame helpDialog = new LatexCommandReferenceDialog(this,"LaTeX Command Reference");
    helpDialog.show();
  }

  private void help_legalFormattingCharacters_actionPerformed(ActionEvent e)
  {
    JFrame helpDialog = new HelpFileViewer(this,"Legal Formatting Characters","legalFormattingCharacters.html");
    helpDialog.setLocationRelativeTo(null);
    helpDialog.show();
  }

  private void tabularMenuItem_actionPerformed(final ActionEvent e)
  {
    String newText = "% sample 3-column table\n";
    newText += "\\begin{tabular}{l|c|r}\n";
    newText += " \\hline\n";
    newText += " R1C1 & R1C2 & R1C3\\\\\n";
    newText += " \\hline\n";
    newText += " R2C1 & R2C2 & R2C3\\\\\n";
    newText += " \\hline\n";
    newText += "\\end{tabular}\n";
    replaceSelectionAndKeepCursor(newText);
  }

  private void rgaEditorPane_keyPressed(final KeyEvent e)
  {
    // convert TAB (w/ selected text) by shifting all text over three
    if ( (e.getKeyCode()==TAB_KEY) && (!e.isShiftDown()) && (rgaEditingArea.getSelectedText()!=null) )
    {
      String textAfterTabbing = EditActions.insertTabAtBeginningOfLine(rgaEditingArea.getSelectedText());
      int start = rgaEditingArea.getSelectionStart();
      int end = rgaEditingArea.getSelectionEnd();
      int originalLength = end-start;
      replaceSelectionAndKeepCursor(textAfterTabbing);
      e.consume();
      int newLength = textAfterTabbing.length();
      rgaEditingArea.select(start,end+newLength-originalLength);
    }
    // convert TAB (w/ no selected text) to spaces
    else if ( (e.getKeyCode()==TAB_KEY) && (!e.isShiftDown()) && (rgaEditingArea.getSelectedText()==null) )
    {
      String textAfterTabbing = TAB_AS_SPACES;
      replaceSelectionAndKeepCursor(textAfterTabbing);
      e.consume();
    }
    // SHIFT-TAB w/ selected text
    else if ( (e.getKeyCode()==TAB_KEY) && (e.isShiftDown()) && (rgaEditingArea.getSelectedText()!=null) )
    {
      String textAfterTabbing = EditActions.removeTabFromBeginningOfLine(rgaEditingArea.getSelectedText());
      int start = rgaEditingArea.getSelectionStart();
      int end = rgaEditingArea.getSelectionEnd();
      int originalLength = end-start;
      replaceSelectionAndKeepCursor(textAfterTabbing);
      e.consume();
      int newLength = textAfterTabbing.length();
      rgaEditingArea.select(start,end+newLength-originalLength);
    }
    // SHIFT-TAB w/ NO selected text
    // @todo DON'T KNOW HOW TO DO THIS
    // @todo NEED HELP HERE
    // maybe determine the text range; manually select the text; then do the same as is done for selected text above
    else if ( (e.getKeyCode()==TAB_KEY) && (e.isShiftDown()) && (rgaEditingArea.getSelectedText()==null) )
    {
      Element root = rgaDocument.getDefaultRootElement();
      Element element = root.getElement(currentRow);
      int startOffset = element.getStartOffset();
      int endOffset = element.getEndOffset();
      rgaEditingArea.select(startOffset,endOffset-1);
      String textOfCurrentLine = getTextOfCurrentLine(element);
      String textAfterRemovingTabs = EditActions.removeTabFromBeginningOfLine(textOfCurrentLine);
      replaceSelectionAndKeepCursor(textAfterRemovingTabs);
      e.consume();
      //int originalLength = endOffset-startOffset;
      //int newLength = textAfterRemovingTabs.length();
      //rgaEditingArea.select(startOffset,endOffset+newLength-originalLength);
    }
    else if ( (e.getKeyCode()==KeyEvent.VK_ENTER) )
    {
      indentNewLineToMatchPreviousWikiLine(e);
    }

    // if ( e.isControlDown() && (e.getKeyCode()==77) ) // CTRL-m activates the popup menu
    // if ( e.isControlDown() && (e.getKeyCode()==83) ) // CTRL-s to save

  }

  // if the previous line is a properly-spaced wiki indent line (i.e., like "   *"),
  // indent the new line to the same level
  private void indentNewLineToMatchPreviousWikiLine(final KeyEvent e)
  {
    Element root = rgaDocument.getDefaultRootElement();
    Element element = root.getElement(currentRow);
    String textOnLineWeAreLeaving = getTextOfCurrentLine(element);
    // if the line begins w/ a bunch of spaces and then an asterisk, start the newline
    // at the position of the asterisk
    int indentationLevel = HTMLStringUtils.getIndentationLevel(textOnLineWeAreLeaving);
    StringBuffer leadingSpaces = new StringBuffer();
    for ( int i=0; i<indentationLevel; i++ )
    {
      leadingSpaces.append(TAB_AS_SPACES);
    }
    String textAfterTabbing = "\n" + leadingSpaces.toString();
    replaceSelectionAndKeepCursor(textAfterTabbing);
    e.consume();
  }

  private String getTextOfCurrentLine(Element element)
  {
    try
    {
      return element.getDocument().getText( element.getStartOffset(),(element.getEndOffset()-element.getStartOffset()) );
    }
    catch (BadLocationException e)
    {
      // this is not a great way to do this, but hopefully it doesn't matter
      e.printStackTrace();
      return "";
    }
  }

  void edit_FindMenuItem_actionPerformed(final ActionEvent e)
  {
    showFindDialog();
  }

  private void showFindDialog()
  {
    FindDialog findDialog = new FindDialog(this,"",false);
    findDialog.setLocationRelativeTo(this);
    findDialog.setVisible(true);
  }

  private void rgaEditorPane_caretUpdate(final CaretEvent e)
  {
    Element root = rgaDocument.getDefaultRootElement();
    int dot = e.getDot();
    int row = root.getElementIndex( dot );
    int col = dot - root.getElement( row ).getStartOffset();
    currentRow = row;
    currentCol = col;
    updateStatusBar(row+1, col+1);
  }

  private void updateStatusBar(int row, int col)
  {
    statusBar.setText( " row: " + row + ", col: " + col );
  }

  public int findTextAndReturnLocation(String textToFind,
                                       final int whereToStartSearch,
                                       final boolean caseSensitiveSearch)
  {
    String currentText = rgaEditingArea.getText();
    if ( !caseSensitiveSearch )
    {
      textToFind = textToFind.toUpperCase();
      currentText = currentText.toUpperCase();
    }
    int textLocation = currentText.indexOf(textToFind,whereToStartSearch+1);
    if ( textLocation >= 0 )
    {
      rgaEditingArea.setCaretPosition(textLocation);
      rgaEditingArea.setSelectionStart(textLocation);
      rgaEditingArea.setSelectionEnd(textLocation+textToFind.length());
      return textLocation;
    }
    else
    {
      return textLocation;
    }
  }

  public java.util.List getStuffSearchForPreviously()
  {
    return stuffSearchForPreviously;
  }

  public void rememberItemSearchedFor(final String item)
  {
    if ( stuffSearchForPreviously.indexOf(item) < 0 )
    {
      stuffSearchForPreviously.add(item);
    }
  }

  void edit_PopupMenuMenuItem_actionPerformed(final ActionEvent e)
  {
    // i know that "show" is deprecated, but i could not get this to work
    // any other way
    Point point = rgaEditingArea.getCaret().getMagicCaretPosition();
    jPopupMenu1.show( rgaEditingArea,
                      (int)point.getX(),
                      (int)point.getY());
    jPopupMenu1.requestFocus();
    jPopupMenu1.grabFocus();
  }

  private void edit_fontMenuItem_actionPerformed(ActionEvent e)
  {    
    // TODO This work is started, still much to do
    FontChooser chooser = new FontChooser(this);
    chooser.setModal(true);
    chooser.setVisible(true);
    
//    Font f = JFontChooser.showDialog(this);
//    if (f!=null)
//    {
//      this.rgaEditingArea.setFont(f);
//    }
  }

  void jTabbedPane_stateChanged(final ChangeEvent e)
  {
    JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
    int index = tabbedPane.getSelectedIndex();
    //System.err.println("index = " + index);
    if (index == 1)
    {
      //System.err.println("about to update the latexViewEditorPane");
      updateLatexEditorPane();
    }
    else if (index == 2)
    {
      updateHTMLEditorPane();
    }
    else if (index == 3)
    {
      updateHTMLPreviewPane();
    }
  }

  private void updateHTMLPreviewPane()
  {
    
    HTMLEditorKit kit = new HTMLEditorKit();
    htmlPreviewPane.setEditorKit(kit);
    StyleSheet styleSheet = kit.getStyleSheet();
    styleSheet.addRule("body {color:#000; font-family: times; font-size: 12px; }");
    styleSheet.addRule("h1 {color: blue;}");
    styleSheet.addRule("h2 {color: blue;}");
    styleSheet.addRule("h3 {color: blue;}");
    styleSheet.addRule("pre {font : 11px monaco; color : black; background-color : #fafafa; }");
    Document doc = kit.createDefaultDocument();
    htmlPreviewPane.setDocument(doc);
    
    try
    {
      // create the html and place it in our pane
      htmlPreviewPane.setText(HTMLStringUtils.createHTMLStringFromWikiString(rgaEditingArea.getText()));
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  private void updateHTMLEditorPane()
  {
    try
    {
      // set the text, then position the caret
      htmlViewEditorPane.setText(HTMLStringUtils.createHTMLStringFromWikiString(rgaEditingArea.getText()));
      // i can't really position the caret in the correct place, b/c the html content is different
      // than the raw content; i could do more work here, but this is ok for now
      htmlViewEditorPane.setCaretPosition(rgaEditingArea.getCaretPosition());
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  private void updateLatexEditorPane()
  {
    try
    {
      latexViewEditorPane.setText(StringUtils.createLatexStringFromWikiString(rgaEditingArea.getText()));
      latexViewEditorPane.setCaretPosition(rgaEditingArea.getCaretPosition());
    }
    catch (IOException e1)
    {
      e1.printStackTrace();
    }
  }

  void hideAllAdornments()
  {
    menuBar.setVisible(false);
    toolBar.setVisible(false);
    statusBar.setVisible(false);
  }

  void showAllAdornments()
  {
    menuBar.setVisible(true);
    toolBar.setVisible(true);
    statusBar.setVisible(true);
  }

  class UndoHandler implements UndoableEditListener
  {

    /**
     * Messaged when the Document has created an edit, the edit is
     * added to <code>undoManager</code>, an instance of UndoManager.
     */
    public void undoableEditHappened(UndoableEditEvent e)
    {
      undoManager.addEdit(e.getEdit());
      undoAction.update();
      redoAction.update();
    }
  }


class UndoAction extends AbstractAction
{
  public UndoAction()
  {
    super("Undo");
    setEnabled(false);
  }

  public void actionPerformed(ActionEvent e)
  {
    try
    {
      undoManager.undo();
    }
    catch (CannotUndoException ex)
    {
      System.out.println("Unable to undoManager: " + ex);
      ex.printStackTrace();
    }
    update();
    redoAction.update();
  }

  protected void update()
  {
    if (undoManager.canUndo())
    {
      setEnabled(true);
      putValue(Action.NAME, undoManager.getUndoPresentationName());
    }
    else
    {
      setEnabled(false);
      putValue(Action.NAME, "Undo");
    }
  }
}



  class RedoAction extends AbstractAction
  {
    public RedoAction()
    {
      super("Redo");
      setEnabled(false);
    }

    public void actionPerformed(ActionEvent e)
    {
      try
      {
        undoManager.redo();
      }
      catch (CannotRedoException ex)
      {
        System.out.println("Unable to redo: " + ex);
        ex.printStackTrace();
      }
      update();
      undoAction.update();
    }

    protected void update()
    {
      if (undoManager.canRedo())
      {
        setEnabled(true);
        putValue(Action.NAME, undoManager.getRedoPresentationName());
      }
      else
      {
        setEnabled(false);
        putValue(Action.NAME, "Redo");
      }
    }
  }

  /** preferences support -- component moved */
  void this_componentMoved(ComponentEvent e) {
    updateDimensions();
  }

  /** preferences support -- component resized */
  void this_componentResized(ComponentEvent e) {
    updateDimensions();
  }

  private void updateDimensions() {
    int x = this.getX();
    int y = this.getY();
    int height = this.getHeight();
    int width = this.getWidth();
    prefs.put(THE_X, Integer.toString(x));
    prefs.put(THE_Y, Integer.toString(y));
    prefs.put(THE_HEIGHT, Integer.toString(height));
    prefs.put(THE_WIDTH, Integer.toString(width));
  }

  public void keyPressed(KeyEvent e)
  {
    KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    
    if (e.getKeyCode() == KeyEvent.VK_R) 
    {
      // not sure what i was doing here, maybe just trying to get the letter 'R'
      // to work in a test?
      //System.err.println("GOT THE R");
    }
    if (e.getKeyCode() == KeyEvent.VK_R && e.isMetaDown()) 
    {
      openRecentFileController.doDisplayDialogAction();
    }
  }

  public void keyReleased(KeyEvent e)
  {
    // TODO Auto-generated method stub
    
  }


  public void keyTyped(KeyEvent e)
  {
    // TODO Auto-generated method stub
    
  }

  // end of MainFrame.java
}

class MainFrame_componentAdapter extends java.awt.event.ComponentAdapter {
  MainFrame adaptee;

  MainFrame_componentAdapter(MainFrame adaptee) {
    this.adaptee = adaptee;
  }
  public void componentMoved(ComponentEvent e) {
    adaptee.this_componentMoved(e);
  }
  public void componentResized(ComponentEvent e) {
    adaptee.this_componentResized(e);
  }
}


