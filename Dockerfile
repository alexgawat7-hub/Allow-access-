import cv2
import os
from datetime import datetime

def main():
    os.makedirs("output", exist_ok=True)

    # 0 = webcam sa host
    cap = cv2.VideoCapture(0)

    if not cap.isOpened():
        print("Hindi ma-open yung camera. Check mo kung naka --device=/dev/video0 ka.")
        return

    cap.set(cv2.CAP_PROP_FRAME_WIDTH, 1280)
    cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 720)

    # Gagawa ng video file na may date
    filename = f"output/record_{datetime.now().strftime('%Y-%m-%d_%H-%M-%S')}.mp4"
    fourcc = cv2.VideoWriter_fourcc(*'mp4v')
    out = cv2.VideoWriter(filename, fourcc, 20.0, (1280, 720))

    print(f"Recording... Naka-save sa {filename}")
    print("Press 'q' sa preview para mag-stop, 's' para screenshot")

    while True:
        ret, frame = cap.read()
        if not ret:
            break

        cv2.putText(frame, "LIVE - REC - Press Q to Stop", (10, 30),
                    cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)

        out.write(frame)

        # Sa Docker, kung walang display, comment mo lang tong imshow
        try:
            cv2.imshow("Camera - LIVE", frame)
        except:
            pass

        key = cv2.waitKey(1) & 0xFF
        if key == ord('q'):
            break
        elif key == ord('s'):
            shot = f"output/shot_{datetime.now().strftime('%H-%M-%S')}.jpg"
            cv2.imwrite(shot, frame)
            print(f"Na-save: {shot}")

    cap.release()
    out.release()
    cv2.destroyAllWindows()
    print("Tapos na. Nasa output/ folder mo na.")

if __name__ == "__main__":
    main()
