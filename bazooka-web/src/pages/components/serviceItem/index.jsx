import React, { Fragment } from 'react';
import { Card, Dropdown, Menu, Icon, Tag } from "antd";
import styles from "../../index.less";
import { COLOR_SHOW } from '@/common/constant';

class ServiceItem extends React.Component {
  constructor(props) {
    super(props);
  }
  static defaultProps = {
    toTop: () => { },
    onItemClick: () => { }
  };
  //置顶
  onTop = () => {
    const { toTop } = this.props;
    toTop();
  }

  renderMoreOpera(item) {
    return (
      <Menu>
        <Menu.Item onClick={() => { this.onTop() }}>
          <span>{item.orderId > 0 ? '取消置顶' : '置顶'}</span>
        </Menu.Item>
      </Menu>
    )
  }
  render() {
    const { item, onItemClick } = this.props;
    return (
      <Card className={`${styles.marginB} ${styles.listItem}`} hoverable>
        <div onClick={() => onItemClick()}>
          <div className={styles.flexCenter}>
            <div className={styles.itemFirst} style={{ backgroundColor: COLOR_SHOW[(item.id) % 5] }}>{item.appName && item.appName.charAt(0).toUpperCase()}</div>
            <div className={styles.appName}>{item.projectName} / {item.appName}</div>
            {
              item.orderId > 0 &&
              <div className={styles.marginL10}>
                <Tag color="blue">置顶</Tag>
              </div>
            }

          </div>
          <div className={styles.itemDesc}>
            <span className={styles.itemDescSpan}>{item.description}</span>
          </div>
        </div>
        <div className={styles.moreOprea}>
          <Dropdown overlay={this.renderMoreOpera(item)} placement="bottomCenter">
            <Icon type="ellipsis" style={{ fontSize: 25 }} />
          </Dropdown>
        </div>
      </Card>
    )
  }

}
export default ServiceItem;