/* eslint-disable import/no-extraneous-dependencies */
import generate from '@ant-design/colors/lib/generate';
import client from 'webpack-theme-color-replacer/client';

export default {
  primaryColor: '#1890ff',
  getAntdSerials(color) {
    // 淡化（即less的tint）
    const lightens = new Array(9).fill().map((t, i) => {
      return client.varyColor.lighten(color, i / 10);
    });
    const colorPalettes = generate(color);
    return lightens.concat(colorPalettes);
  },
  changeColor(newColor) {
    const lastColor = this.lastColor || this.primaryColor;
    const options = {
      cssUrl: '/css/theme-colors.css', // hash模式下用相对路径
      oldColors: this.getAntdSerials(lastColor), // current colors array. The same as `matchColors`
      newColors: this.getAntdSerials(newColor || this.primaryColor), // new colors array, one-to-one corresponde with `oldColors`
    };
    const promise = client.changer.changeColor(options, Promise);
    this.lastColor = lastColor;
    return promise;
  },
};
