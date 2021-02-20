package Scanner;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import net.kanjitomo.*;

import java.awt.*;

public class KanjiScanner {

    private final Robot robot;
    private final KanjiTomo kanjiTomo;

    public KanjiScanner() throws AWTException {
        this.robot = new Robot();
        this.kanjiTomo = new KanjiTomo();
        this.kanjiTomo.setOrientation(Orientation.VERTICAL);
        this.kanjiTomo.setCharacterColor(CharacterColor.BLACK_ON_WHITE);
        this.kanjiTomo.setDictionary(DictionaryType.JAPANESE_DEFAULT, DictionaryType.JAPANESE_NAMES);

        //new Translator();
    }

    /**
     * Scan the current hovered window for kanji & return any results we get
     * @return kanji OCR Results
     * @throws Exception error from ocr scan
     */
    public OCRResults scan() throws Exception {
        var hwnd = User32.INSTANCE.GetForegroundWindow();
        var win32Rect = new WinDef.RECT();
        User32.INSTANCE.GetWindowRect(hwnd, win32Rect);

        var rectangle = win32Rect.toRectangle();
        var mousePosition = MouseInfo.getPointerInfo().getLocation();
        var relativeX = mousePosition.x - rectangle.x;
        var relativeY = mousePosition.y - rectangle.y;

        var image = robot.createScreenCapture(rectangle);

        kanjiTomo.setTargetImage(image);
        return kanjiTomo.runOCR(new Point(relativeX, relativeY));
    }
}
