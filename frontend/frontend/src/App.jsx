import { useState, useRef } from "react"
import "./App.css"

export default function App() {
  const [step, setStep] = useState(1)
  const [consent, setConsent] = useState(false)

  const [passportMode, setPassportMode] = useState("upload") // "upload" | "camera"
  const [passportFile, setPassportFile] = useState(null)

  const [selfieFile, setSelfieFile] = useState(null)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState("")
  const [result, setResult] = useState(null)

  // Selfie camera refs
  const videoRef = useRef(null)
  const canvasRef = useRef(null)
  const streamRef = useRef(null)

  // Passport camera refs
  const passportVideoRef = useRef(null)
  const passportCanvasRef = useRef(null)
  const passportStreamRef = useRef(null)

  function nextFromConsent() {
    if (!consent) {
      setError("Please agree to continue.")
      return
    }
    setError("")
    setStep(2)
  }

  function onPassportChange(e) {
    const file = e.target.files[0]
    if (!file) return
    if (!file.type.startsWith("image/")) {
      setError("Passport must be an image file.")
      return
    }
    setError("")
    setPassportFile(file)
  }

  function nextFromPassport() {
    if (!passportFile) {
      setError("Please upload or capture your passport photo.")
      return
    }
    setError("")
    stopPassportCamera()
    setStep(3)
  }

  async function startSelfieCamera() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true })
      streamRef.current = stream
      if (videoRef.current) {
        videoRef.current.srcObject = stream
        videoRef.current.play()
      }
    } catch {
      setError("Could not access camera. Check permissions.")
    }
  }

  function stopSelfieCamera() {
    if (streamRef.current) {
      streamRef.current.getTracks().forEach(t => t.stop())
      streamRef.current = null
    }
  }

  function captureSelfie() {
    if (!videoRef.current || !canvasRef.current) return
    const video = videoRef.current
    const canvas = canvasRef.current
    canvas.width = video.videoWidth || 640
    canvas.height = video.videoHeight || 480
    const ctx = canvas.getContext("2d")
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    canvas.toBlob(blob => {
      if (blob) {
        const file = new File([blob], "selfie.jpg", { type: "image/jpeg" })
        setSelfieFile(file)
        setError("")
      }
    }, "image/jpeg")
  }

  // Passport camera functions
  async function startPassportCamera() {
    try {
      const stream = await navigator.mediaDevices.getUserMedia({ video: true })
      passportStreamRef.current = stream
      if (passportVideoRef.current) {
        passportVideoRef.current.srcObject = stream
        passportVideoRef.current.play()
      }
    } catch {
      setError("Could not access camera for passport. Check permissions.")
    }
  }

  function stopPassportCamera() {
    if (passportStreamRef.current) {
      passportStreamRef.current.getTracks().forEach(t => t.stop())
      passportStreamRef.current = null
    }
  }

  function capturePassport() {
    if (!passportVideoRef.current || !passportCanvasRef.current) return
    const video = passportVideoRef.current
    const canvas = passportCanvasRef.current
    canvas.width = video.videoWidth || 640
    canvas.height = video.videoHeight || 480
    const ctx = canvas.getContext("2d")
    ctx.drawImage(video, 0, 0, canvas.width, canvas.height)
    canvas.toBlob(blob => {
      if (blob) {
        const file = new File([blob], "passport.jpg", { type: "image/jpeg" })
        setPassportFile(file)
        setError("")
      }
    }, "image/jpeg")
  }

  async function submitVerification() {
    if (!passportFile || !selfieFile || !consent) {
      setError("Missing data to submit.")
      return
    }

    setLoading(true)
    setError("")
    setResult(null)

    try {
      const form = new FormData()
      form.append("passport", passportFile)
      form.append("selfie", selfieFile)
      form.append("consent", "true")

      const res = await fetch("/api/verification?consent=true", {
        method: "POST",
        body: form
      })

      if (!res.ok) {
        let msg = "Verification failed."
        try {
          const data = await res.json()
          if (data.error) msg = data.error
        } catch {}
        throw new Error(msg)
      }

      const data = await res.json()
      setResult(data)
      setStep(4)
    } catch (e) {
      setError(e.message)
    } finally {
      setLoading(false)
      stopSelfieCamera()
      stopPassportCamera()
    }
  }

  function resetWizard() {
    stopSelfieCamera()
    stopPassportCamera()
    setStep(1)
    setConsent(false)
    setPassportMode("upload")
    setPassportFile(null)
    setSelfieFile(null)
    setResult(null)
    setError("")
    setLoading(false)
  }

  function stepLabel(n) {
    if (n === 1) return "Consent"
    if (n === 2) return "Passport"
    if (n === 3) return "Selfie"
    if (n === 4) return "Result"
    return ""
  }

  return (
    <div className="app-root">
      <div className="card">
        <div className="card-header">
          <div>
            <h1 className="title">Guest Verification</h1>
            <p className="subtitle">Secure identity check for your booking</p>
          </div>
          <div className="step-indicator">
            <div className="step-count">Step {step} / 4</div>
            <div className="step-label">{stepLabel(step)}</div>
          </div>
        </div>

        <div className="progress">
          <div className="progress-bar" style={{ width: `${(step / 4) * 100}%` }} />
        </div>

        {error && (
          <div className="alert">
            {error}
          </div>
        )}

        <div className="content">
          {step === 1 && (
            <div className="step">
              <p className="text">
                To help protect both guests and hosts, we can verify your identity using your passport and a live photo.
                Your data is transmitted securely and handled with care.
              </p>
              <label className="checkbox-row">
                <input
                  type="checkbox"
                  checked={consent}
                  onChange={e => setConsent(e.target.checked)}
                />
                <span>I consent to this verification process.</span>
              </label>
              <div className="actions">
                <button className="btn primary" onClick={nextFromConsent}>
                  Continue
                </button>
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="step">
              <p className="text">
                Provide a clear photo of the data page of your passport. You can upload from your device or take a live picture.
              </p>

              <div className="actions-row" style={{ marginBottom: 8 }}>
                <button
                  className={`btn secondary ${passportMode === "upload" ? "btn-active" : ""}`}
                  onClick={() => { setPassportMode("upload"); stopPassportCamera(); }}
                >
                  Upload file
                </button>
                <button
                  className={`btn secondary ${passportMode === "camera" ? "btn-active" : ""}`}
                  onClick={() => { setPassportMode("camera"); setPassportFile(null); }}
                >
                  Use camera
                </button>
              </div>

              {passportMode === "upload" && (
                <>
                  <input
                    type="file"
                    accept="image/*"
                    onChange={onPassportChange}
                    className="file-input"
                  />
                  {passportFile && (
                    <p className="hint">
                      Selected file: <strong>{passportFile.name}</strong>
                    </p>
                  )}
                </>
              )}

              {passportMode === "camera" && (
                <>
                  <div className="video-wrapper">
                    <video
                      ref={passportVideoRef}
                      className="video"
                      autoPlay
                      playsInline
                    />
                  </div>
                  <canvas ref={passportCanvasRef} style={{ display: "none" }} />
                  <div className="actions-row">
                    <button className="btn secondary" onClick={startPassportCamera}>
                      Start camera
                    </button>
                    <button className="btn secondary" onClick={capturePassport}>
                      Capture passport
                    </button>
                  </div>
                  {passportFile && (
                    <p className="hint">
                      Passport image captured and ready.
                    </p>
                  )}
                </>
              )}

              <div className="actions">
                <button className="btn secondary" onClick={() => { stopPassportCamera(); setStep(1) }}>
                  Back
                </button>
                <button className="btn primary" onClick={nextFromPassport}>
                  Continue
                </button>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="step">
              <p className="text">
                Turn on your camera, center your face, and capture a selfie. This will be compared to your passport photo.
              </p>
              <div className="video-wrapper">
                <video
                  ref={videoRef}
                  className="video"
                  autoPlay
                  playsInline
                />
              </div>
              <canvas ref={canvasRef} style={{ display: "none" }} />
              <div className="actions-row">
                <button className="btn secondary" onClick={startSelfieCamera}>
                  Start camera
                </button>
                <button className="btn secondary" onClick={captureSelfie}>
                  Capture selfie
                </button>
              </div>
              {selfieFile && (
                <p className="hint">
                  Selfie captured and ready to submit.
                </p>
              )}
              <div className="actions">
                <button className="btn secondary" onClick={() => { stopSelfieCamera(); setStep(2) }}>
                  Back
                </button>
                <button
                  className="btn primary"
                  disabled={loading || !selfieFile}
                  onClick={submitVerification}
                >
                  {loading ? "Submitting..." : "Submit verification"}
                </button>
              </div>
            </div>
          )}

          {step === 4 && result && (
            <div className="step">
              <h2 className="result-title">Verification result</h2>
              <div className="result-box">
                <p>
                  Status: <strong>{result.status}</strong>
                </p>
                <p>
                  Match score: <strong>{(result.score * 100).toFixed(1)}%</strong>
                </p>
                <p className="text">{result.message}</p>
              </div>
              <div className="actions">
                <button className="btn primary" onClick={resetWizard}>
                  Start over
                </button>
              </div>
            </div>
          )}
        </div>

        <p className="footer-note">
          This is a prototype interface. Do not use with real passport data in production without proper legal and security review.
        </p>
      </div>
    </div>
  )
}
