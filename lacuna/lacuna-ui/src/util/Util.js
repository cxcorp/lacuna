const Util = (function () {
    return {
        // http://stackoverflow.com/a/12043228/996081
        hexToRgb: (hex) => {
            if (hex.length < 6) {
                throw new Error('Expected hex color of form #123456');
            }
            if (hex.startsWith('#')) {
                hex = hex.substring(1);
            }
            const rgb = parseInt(hex, 16);
            const r = (rgb >> 16) & 0xFF;
            const g = (rgb >> 8) & 0xFF;
            const b = (rgb >> 0) & 0xFF;
            return { r, g, b };
        },

        // https://www.w3.org/TR/AERT#color-contrast
        rgbToLuma: (r, g, b) => 0.299 * r + 0.587 * g + 0.114 * b,

        isBrightColor: (r, g, b) => Util.rgbToLuma(r, g, b) > 125,

        makePromiseCancelable: (promise) => {
            let hasCanceled_ = false;

            const wrappedPromise = new Promise((resolve, reject) => {
                promise.then((val) =>
                hasCanceled_ ? reject({isCanceled: true}) : resolve(val)
                );
                promise.catch((error) =>
                hasCanceled_ ? reject({isCanceled: true}) : reject(error)
                );
            });

            return {
                promise: wrappedPromise,
                cancel() {
                    hasCanceled_ = true;
                },
            };
        }
    };
})();

export default Util;