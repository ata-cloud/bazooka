import React, { Component } from 'react';
import { Icon, Tooltip } from 'antd';
import { formatMessage } from 'umi-plugin-react/locale';
import HeaderSearch from '../HeaderSearch';
import SelectLang from '../SelectLang';
import styles from './index.less';
import Avatar from './AvatarDropdown';
import { connect } from 'dva';

class GlobalHeaderRight extends Component {
  render() {
    const { theme, layout } = this.props;
    let className = styles.right;

    if (theme === 'dark' && layout === 'topmenu') {
      className = `${styles.right}  ${styles.dark}`;
    }

    return (
      <div className={styles.rightBox}>
        <div></div>
        <div className={className}>
          {/* <HeaderSearch
          className={`${styles.action} ${styles.search}`}
          placeholder={formatMessage({
            id: 'component.globalHeader.search',
          })}
          dataSource={[
            formatMessage({
              id: 'component.globalHeader.search.example1',
            }),
            formatMessage({
              id: 'component.globalHeader.search.example2',
            }),
            formatMessage({
              id: 'component.globalHeader.search.example3',
            }),
          ]}
          onSearch={value => {
            console.log('input', value); // tslint:disable-line no-console
          }}
          onPressEnter={value => {
            console.log('enter', value); // tslint:disable-line no-console
          }}
        />
        <Tooltip
          title={formatMessage({
            id: 'component.globalHeader.help',
          })}
        >
          <a
            target="_blank"
            href="https://pro.ant.design/docs/getting-started"
            rel="noopener noreferrer"
            className={styles.action}
          >
            <Icon type="question-circle-o" />
          </a>
        </Tooltip> */}
         <Tooltip
          title={formatMessage({
            id: 'component.globalHeader.help',
          })}
        >
          <a
            target="_blank"
            href="https://github.com/ata-cloud/ata-cloud-service/blob/master/docs/index.md"
            rel="noopener noreferrer"
            className={styles.action}
          >
            <Icon type="question-circle-o" />
          </a>
        </Tooltip>
          <Avatar />
          {/* <SelectLang className={styles.action} /> */}
        </div>
      </div>

    );
  }
}

export default connect(({ settings }) => ({
  theme: settings.navTheme,
  layout: settings.layout,
}))(GlobalHeaderRight);
